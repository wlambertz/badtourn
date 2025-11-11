package cmd

import (
	"fmt"
	"strings"
	"sync"
	"time"
)

type loader struct {
	message string
	stop    chan struct{}
	wg      sync.WaitGroup
}

func newLoader(message string) *loader {
	if message == "" || flagQuiet {
		return nil
	}
	return &loader{
		message: message,
		stop:    make(chan struct{}),
	}
}

func (l *loader) Start() {
	if l == nil {
		return
	}
	l.wg.Add(1)
	go func() {
		defer l.wg.Done()
		spin := []rune("ｦｧｨｩｪｫｬｭｮｯｱｲｳｴｵｶｷｸｹｺｻｼｽｾｿﾀﾁﾂﾃﾄﾅﾆﾇﾈﾉﾊﾋﾌﾍﾎﾏﾐﾑﾒﾓﾔﾕﾖﾗﾘﾙﾚﾛﾜﾝ")
		idx := 0
		for {
			select {
			case <-l.stop:
				clearLine(len(l.message) + 2)
				return
			default:
				fmt.Printf("\r%s %c", l.message, spin[idx%len(spin)])
				idx++
				time.Sleep(120 * time.Millisecond)
			}
		}
	}()
}

func (l *loader) Stop(final string) {
	if l == nil {
		if final != "" {
			fmt.Println(final)
		}
		return
	}
	close(l.stop)
	l.wg.Wait()
	if final != "" {
		fmt.Printf("\r%s\n", final)
	} else {
		fmt.Print("\r")
	}
}

func clearLine(width int) {
	if width <= 0 {
		width = 40
	}
	fmt.Printf("\r%s\r", strings.Repeat(" ", width))
}
