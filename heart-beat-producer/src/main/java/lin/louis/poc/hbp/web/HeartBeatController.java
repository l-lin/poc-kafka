package lin.louis.poc.hbp.web;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lin.louis.poc.hbp.dto.HeartBeatDTO;
import lin.louis.poc.hbp.repository.HeartBeatRepository;
import lin.louis.poc.models.HeartBeat;


@RestController
@RequestMapping(path = "/heart-beats")
public class HeartBeatController {

	private final HeartBeatRepository heartBeatRepository;

	public HeartBeatController(HeartBeatRepository heartBeatRepository) {
		this.heartBeatRepository = heartBeatRepository;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void create(@RequestBody HeartBeatDTO heartBeatDTO) {
		var heartBeat = HeartBeat.newBuilder()
				.setUserId(heartBeatDTO.getUserId())
				.setHri(heartBeatDTO.getHri())
				.setQrs(heartBeatDTO.getQrs())
				.setTimestamp(Instant.now())
				.build();
		heartBeatRepository.save(heartBeat);
	}
}
