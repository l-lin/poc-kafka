package heartbeat

import "fmt"

// HeartBeat at one instant
type HeartBeat struct {
	UserID    int `json:"userId"`
	HRI       int `json:"hri"`
	QRS       `json:"qrs"`
	Timestamp int64 `json:"timestamp"`
}

func (hb HeartBeat) String() string {
	return fmt.Sprintf(`UserID: %d,
HRI: %d,
QRS: %s,
Timestamp: %d
`, hb.UserID, hb.HRI, hb.QRS, hb.Timestamp)
}

// Builder helps creating new heartbeats
type Builder interface {
	Build() HeartBeat
}
