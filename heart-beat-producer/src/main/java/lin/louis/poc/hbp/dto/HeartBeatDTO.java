package lin.louis.poc.hbp.dto;

import lin.louis.poc.models.HeartBeatQRS;


public class HeartBeatDTO {

	private long userId;

	private int hri;

	private HeartBeatQRS qrs;

	public HeartBeatDTO() {}

	public HeartBeatDTO(long userId, int hri, HeartBeatQRS qrs) {
		this.userId = userId;
		this.hri = hri;
		this.qrs = qrs;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public int getHri() {
		return hri;
	}

	public void setHri(int hri) {
		this.hri = hri;
	}

	public HeartBeatQRS getQrs() {
		return qrs;
	}

	public void setQrs(HeartBeatQRS qrs) {
		this.qrs = qrs;
	}
}
