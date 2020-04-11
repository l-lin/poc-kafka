package lin.louis.poc.hrc.dto;

public class UserDTO {
	private long userId;

	public UserDTO() {
	}

	public UserDTO(long userId) {
		this.userId = userId;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}
}
