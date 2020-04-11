package lin.louis.poc.hbp.controller;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lin.louis.poc.hbp.dto.HeartBeatDTO;
import lin.louis.poc.hbp.repository.HBRepository;
import lin.louis.poc.models.HeartBeat;


@RestController
@RequestMapping(path = "/heart-beats")
public class HBController {

	private final HBRepository hbRepository;

	public HBController(HBRepository hbRepository) {
		this.hbRepository = hbRepository;
	}

	/**
	 * Simple endpoint the smartwatch can attack to register a new heart beat
	 */
	@PostMapping
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void create(@RequestBody HeartBeatDTO heartBeatDTO) {
		var heartBeat = HeartBeat.newBuilder()
								 .setUserId(heartBeatDTO.getUserId())
								 .setHri(heartBeatDTO.getHri())
								 .setQrs(heartBeatDTO.getQrs())
								 .setTimestamp(Instant.now())
								 .build();
		hbRepository.save(heartBeat);
	}
}
