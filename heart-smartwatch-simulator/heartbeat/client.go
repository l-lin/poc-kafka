package heartbeat

import (
	"bytes"
	"encoding/json"
	"log"
	"net/http"
)

// Send the heartbeat to the heart-beat-producer
func Send(url string, hb HeartBeat) {
	reqBody, err := json.Marshal(hb)
	if err != nil {
		log.Println(err)
		return
	}
	resp, err := http.Post(url, "application/json", bytes.NewBuffer(reqBody))
	if err != nil {
		log.Println(err)
		return
	}
	if resp.StatusCode != 204 {
		log.Printf("Response status was not 204, but %d\n", resp.StatusCode)
	}
}
