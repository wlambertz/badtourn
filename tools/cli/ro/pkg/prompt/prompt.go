package prompt

import (
	"bufio"
	"fmt"
	"os"
	"strings"
)

func Confirm(question string, defaultYes bool) bool {
	in := bufio.NewReader(os.Stdin)
	def := "y/N"
	if defaultYes {
		def = "Y/n"
	}
	fmt.Printf("%s [%s]: ", question, def)
	line, _ := in.ReadString('\n')
	line = strings.TrimSpace(strings.ToLower(line))
	if line == "" {
		return defaultYes
	}
	return line == "y" || line == "yes"
}

func Input(question string, defaultValue string) string {
	in := bufio.NewReader(os.Stdin)
	prompt := question
	if strings.TrimSpace(defaultValue) != "" {
		prompt = fmt.Sprintf("%s (%s)", question, defaultValue)
	}
	fmt.Printf("%s: ", prompt)
	text, _ := in.ReadString('\n')
	text = strings.TrimSpace(text)
	if text == "" {
		return defaultValue
	}
	return text
}
