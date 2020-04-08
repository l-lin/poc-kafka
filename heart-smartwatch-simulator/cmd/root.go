package cmd

import (
	"encoding/json"
	"fmt"
	"log"
	"math/rand"
	"os"
	"strconv"
	"strings"
	"time"

	"github.com/l-lin/poc/kafka-streams/heart-smartwatch-simulator/heartbeat"
	"github.com/spf13/cobra"
)

var (
	rootCmd = &cobra.Command{
		Use:   "heart-smartwatch-simulator",
		Short: "Fake smartwatch application",
		Long:  `Application that simulates heartbeats and send HTTP requests to heart-beat-producer`,
		Run:   run,
	}
	hriMin, hriMax       int
	nbUsers              int
	percentFailures      string
	heartBeatProducerURL string
)

// Execute adds all child commands to the root command and sets flags appropriately.
// This is called by main.main(). It only needs to happen once to the rootCmd.
func Execute(version, buildDate string) {
	rootCmd.Version = func(version, buildDate string) string {
		res, err := json.Marshal(cliBuild{Version: version, BuildDate: buildDate})
		if err != nil {
			log.Fatal(err)
		}
		return string(res)
	}(version, buildDate)
	if err := rootCmd.Execute(); err != nil {
		fmt.Println(err)
		os.Exit(1)
	}
}

func init() {
	rootCmd.SetVersionTemplate(`{{printf "%s" .Version}}`)
	rootCmd.Flags().IntVar(&hriMin, "hri-min", 0, "Min instant frequency of a heartbeat")
	rootCmd.Flags().IntVar(&hriMax, "hri-max", 250, "Max instant frequency of a heartbeat")
	rootCmd.Flags().IntVar(&nbUsers, "nb-users", 5, "Number of users to simulate")
	rootCmd.Flags().StringVar(&percentFailures, "percent-failures", "0,5,10,15,50", "Percentages of failure to set for the users, separated by a comma. The indexes match the user to set the percentage of failure")
	rootCmd.Flags().StringVar(&heartBeatProducerURL, "heart-beat-producer", "http://localhost:8180/heart-beats", "URL of the heart-beat-producer service to send heart beats")
}

type cliBuild struct {
	Version   string `json:"version"`
	BuildDate string `json:"buildDate"`
}

func run(cmd *cobra.Command, args []string) {
	userPercents := buildUserPercents(percentFailures)
	var neverFinish chan bool
	log.Printf("Starting to simulate %d smartwatches...", nbUsers)
	for i := 0; i < nbUsers; i++ {
		percent := 0
		if i < len(userPercents) {
			percent = userPercents[i]
		}
		go func(userID, percent int) {
			var r heartbeat.Builder
			r, err := heartbeat.NewRandom(userID, hriMin, hriMax, percent)
			if err != nil {
				log.Fatal(err)
			}
			log.Printf("user %d: starting to simulate heartbeats with %d%% failure\n", userID, percent)
			for {
				heartbeat.Send(heartBeatProducerURL, r.Build())
				t := rand.Intn(3) + 1
				time.Sleep(time.Duration(t) * time.Second)
			}
		}(i+1, percent)
	}
	<-neverFinish
}

func buildUserPercents(percentFailures string) []int {
	arr := strings.Split(percentFailures, ",")
	userPercents := make([]int, len(arr))
	for i := 0; i < len(arr); i++ {
		var err error
		if userPercents[i], err = strconv.Atoi(arr[i]); err != nil {
			userPercents[i] = 0
		}
	}
	return userPercents
}
