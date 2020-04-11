package heartbeat

import (
	"fmt"
	"testing"
)

func TestRandom_Build(t *testing.T) {
	r, _ := NewRandom(123, 0, 250, 0, true)
	hb := r.Build()
	fmt.Println(hb)
}
