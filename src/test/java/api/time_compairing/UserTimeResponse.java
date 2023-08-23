package api.time_compairing;

import lombok.Getter;

@Getter
public class UserTimeResponse extends UserTime{

    private String updatedAt;
    public UserTimeResponse(String name, String job, String updatedAt) {
        super(name, job);
        this.updatedAt = updatedAt;
    }
}
