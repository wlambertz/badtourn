package execx

import (
    "bytes"
    "context"
    "errors"
    "fmt"
    "io"
    "os"
    "os/exec"
    "strings"
    "time"
)

type RunOptions struct {
    Cmd           []string
    Cwd           string
    Env           map[string]string
    Timeout       time.Duration
    Interactive   bool
    Redact        []string
    DryRun        bool
    CaptureOutput bool
    Stdout        io.Writer
    Stderr        io.Writer
}

type ExitError struct {
    Code int
    Err  error
}

func (e *ExitError) Error() string { return e.Err.Error() }

func Run(ctx context.Context, opts RunOptions) (int, string, string, error) {
    if len(opts.Cmd) == 0 {
        return 2, "", "", errors.New("empty command")
    }
    if opts.DryRun {
        return 0, "", "", nil
    }

    if opts.Timeout > 0 {
        var cancel context.CancelFunc
        ctx, cancel = context.WithTimeout(ctx, opts.Timeout)
        defer cancel()
    }

    name := opts.Cmd[0]
    args := opts.Cmd[1:]
    command := exec.CommandContext(ctx, name, args...)
    if opts.Cwd != "" {
        command.Dir = opts.Cwd
    }
    if len(opts.Env) > 0 {
        env := os.Environ()
        for k, v := range opts.Env {
            env = append(env, fmt.Sprintf("%s=%s", k, v))
        }
        command.Env = env
    }

    var stdoutBuf, stderrBuf bytes.Buffer
    var stdoutW io.Writer = &stdoutBuf
    var stderrW io.Writer = &stderrBuf
    if !opts.CaptureOutput {
        stdoutW = os.Stdout
        stderrW = os.Stderr
    }
    if opts.Stdout != nil { stdoutW = io.MultiWriter(stdoutW, opts.Stdout) }
    if opts.Stderr != nil { stderrW = io.MultiWriter(stderrW, opts.Stderr) }

    command.Stdout = stdoutW
    command.Stderr = stderrW
    if opts.Interactive {
        command.Stdin = os.Stdin
    }

    err := command.Run()
    stdout := stdoutBuf.String()
    stderr := stderrBuf.String()
    if len(opts.Redact) > 0 {
        for _, r := range opts.Redact {
            if r == "" { continue }
            stdout = strings.ReplaceAll(stdout, r, "***")
            stderr = strings.ReplaceAll(stderr, r, "***")
        }
    }
    if err == nil {
        return 0, stdout, stderr, nil
    }
    var ee *exec.ExitError
    if errors.As(err, &ee) {
        code := ee.ExitCode()
        return code, stdout, stderr, &ExitError{Code: code, Err: err}
    }
    // context deadline or other errors
    return 1, stdout, stderr, err
}


