package cmd

import "testing"

func TestBuildCommitMessage(t *testing.T) {
	tests := []struct {
		name    string
		input   commitInputs
		want    string
		wantErr bool
	}{
		{
			name: "basic feat",
			input: commitInputs{
				Type:    "feat",
				Summary: "add deploy command",
			},
			want: "feat: add deploy command",
		},
		{
			name: "with scope and wip",
			input: commitInputs{
				Type:    "fix",
				Scope:   "deploy",
				Summary: "handle dry run mode",
				Wip:     true,
			},
			want: "fix(deploy): [WIP] handle dry run mode",
		},
		{
			name: "breaking change",
			input: commitInputs{
				Type:         "feat",
				Scope:        "api",
				Summary:      "rename field",
				Breaking:     true,
				BreakingNote: "clients must update payload",
			},
			want: "feat(api)!: rename field",
		},
		{
			name: "missing summary",
			input: commitInputs{
				Type: "feat",
			},
			wantErr: true,
		},
		{
			name: "missing type",
			input: commitInputs{
				Summary: "test",
			},
			wantErr: true,
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			msg, err := buildCommitMessage(tt.input)
			if tt.wantErr {
				if err == nil {
					t.Fatalf("expected error")
				}
				return
			}
			if err != nil {
				t.Fatalf("unexpected error: %v", err)
			}
			if msg.Subject != tt.want {
				t.Fatalf("subject mismatch: got %q want %q", msg.Subject, tt.want)
			}
			if tt.input.Breaking {
				if len(msg.Bodies) == 0 {
					t.Fatalf("expected breaking note in body")
				}
			}
		})
	}
}
