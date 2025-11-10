package telemetry

import (
	"bytes"
	"encoding/json"
	"log/slog"
	"net/http"
	"runtime"
	"strings"
	"sync"
	"time"

	"github.com/wlambertz/rallyon/tools/cli/ro/pkg/config"
	"github.com/wlambertz/rallyon/tools/cli/ro/pkg/version"
)

type Event struct {
	Command   string            `json:"command"`
	Duration  time.Duration     `json:"duration"`
	ExitCode  int               `json:"exit_code"`
	Success   bool              `json:"success"`
	Timestamp time.Time         `json:"timestamp"`
	Version   string            `json:"version"`
	Commit    string            `json:"commit"`
	OS        string            `json:"os"`
	Arch      string            `json:"arch"`
	Metadata  map[string]string `json:"metadata,omitempty"`
}

type Client struct {
	cfg     *config.Telemetry
	enabled bool
}

var (
	once   sync.Once
	client *Client
)

func Init(cfg *config.Telemetry) {
	once.Do(func() {
		client = &Client{cfg: cfg, enabled: cfg.Enabled && strings.TrimSpace(cfg.Endpoint) != ""}
	})
}

func Enabled() bool {
	if client == nil {
		return false
	}
	return client.enabled
}

func Track(event Event) {
	if !Enabled() {
		return
	}
	if !client.shouldCollect(event.Command) {
		return
	}
	go client.send(event)
}

func (c *Client) send(event Event) {
	event.Timestamp = time.Now()
	if event.OS == "" {
		event.OS = runtime.GOOS
	}
	if event.Arch == "" {
		event.Arch = runtime.GOARCH
	}
	if event.Version == "" {
		event.Version = version.Version
	}
	if event.Commit == "" {
		event.Commit = version.Commit
	}
	payload, err := json.Marshal(event)
	if err != nil {
		return
	}
	req, err := http.NewRequest(http.MethodPost, c.cfg.Endpoint, bytes.NewReader(payload))
	if err != nil {
		return
	}
	req.Header.Set("Content-Type", "application/json")
	httpClient := &http.Client{Timeout: 2 * time.Second}
	if _, err := httpClient.Do(req); err != nil {
		slog.Debug("telemetry send failed", "error", err)
	}
}

func (c *Client) shouldCollect(command string) bool {
	if len(c.cfg.CollectCommands) == 0 {
		return true
	}
	for _, allowed := range c.cfg.CollectCommands {
		if strings.EqualFold(strings.TrimSpace(allowed), strings.TrimSpace(command)) {
			return true
		}
	}
	return false
}
