package heartbeat

import (
	"errors"
	"log"
	"math/rand"
	"time"
)

// Random builder to create random heartbeats
type Random struct {
	UserID, HRIMin, HRIMax int
	PercentFailure         int
}

// NewRandom returns a new builder to create random heartbeats
func NewRandom(userID, hriMin, hriMax, percentFailure int) (Builder, error) {
	if percentFailure < 0 || percentFailure > 100 {
		return Random{}, errors.New("The percentFailure must be between 0 and 100")
	}
	return Random{
		UserID:         userID,
		HRIMin:         hriMin,
		HRIMax:         hriMax,
		PercentFailure: percentFailure,
	}, nil
}

// Build a random heartbeat
func (r Random) Build() HeartBeat {
	hri := randomInt(r.HRIMin, r.HRIMax)
	qrs := randomQRS()
	if r.isFailure() {
		chance := rand.Intn(100)
		if chance < 48 { // 48% chance of simulating an invalid QRS
			log.Printf("user %d: send heartbeat with failed QRS\n", r.UserID)
			qrs = X
		} else if chance < 95 { // 47% chance of simulating an invalid HRI
			log.Printf("user %d: send heartbeat with failed HRI\n", r.UserID)
			hri = randomInt(r.HRIMax, r.HRIMax*5)
		} else { // 5% chance of simulating a gap
			gap := rand.Intn(6) + 5
			log.Printf("user %d: send heartbeat with a %ds gap\n", r.UserID, gap)
			time.Sleep(time.Duration(gap) * time.Second) // minimum 5 seconds gap
		}
	} else {
		log.Printf("user %d: send valid heartbeat\n", r.UserID)
	}
	return HeartBeat{
		UserID:    r.UserID,
		HRI:       hri,
		QRS:       qrs,
		Timestamp: time.Now().UnixNano() / int64(time.Millisecond),
	}
}

func (r Random) isFailure() bool {
	i := rand.Intn(99) + 1
	return i <= r.PercentFailure
}

func isQRSFailure() bool {
	return rand.Intn(100) > 50
}

func randomInt(min, max int) int {
	rand.Seed(time.Now().UnixNano())
	return rand.Intn(max-min+1) + min
}
