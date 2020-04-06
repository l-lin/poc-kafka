//go:generate stringer -type=QRS
package heartbeat

// QRS is the event describing the activity of a heartbeat
type QRS int

const (
	A QRS = iota
	V
	N
	F
	P
	X
)

// validQRS contains only valid heartbeat QRS
var validQRS = []QRS{A, V, N, F, P}

func randomQRS() QRS {
	return validQRS[randomInt(0, len(validQRS)-1)]
}
