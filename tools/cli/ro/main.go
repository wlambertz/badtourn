package main

import (
    "os"

    "github.com/wlambertz/rallyon/tools/cli/ro/pkg/cmd"
)

func main() {
    if err := cmd.Execute(); err != nil {
        os.Exit(1)
    }
}


