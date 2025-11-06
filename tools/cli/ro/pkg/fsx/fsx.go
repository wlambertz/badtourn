package fsx

import (
    "os"
    "path/filepath"
)

// FindUpward searches upwards from start for any of the provided names.
func FindUpward(start string, names ...string) (string, bool) {
    dir := start
    for {
        for _, n := range names {
            p := filepath.Join(dir, n)
            if _, err := os.Stat(p); err == nil {
                return dir, true
            }
        }
        parent := filepath.Dir(dir)
        if parent == dir {
            return "", false
        }
        dir = parent
    }
}


