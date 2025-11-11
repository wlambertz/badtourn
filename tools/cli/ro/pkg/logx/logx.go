package logx

import (
    "io"
    "log/slog"
    "os"
)

type Options struct {
	JSON    bool
	Verbose bool
	Writer  io.Writer
	Quiet   bool
}

func New(opts Options) *slog.Logger {
	if opts.Writer == nil {
		opts.Writer = os.Stdout
	}
	var handler slog.Handler
	if opts.JSON {
		writer := opts.Writer
		if opts.Quiet {
			writer = io.Discard
		}
		handler = slog.NewJSONHandler(writer, &slog.HandlerOptions{Level: level(opts.Verbose)})
	} else {
		writer := opts.Writer
		if opts.Quiet {
			writer = io.Discard
		}
		handler = slog.NewTextHandler(writer, &slog.HandlerOptions{Level: level(opts.Verbose)})
	}
	return slog.New(handler)
}

func level(verbose bool) slog.Leveler {
    if verbose {
        l := slog.LevelDebug
        return l
    }
    l := slog.LevelInfo
    return l
}
