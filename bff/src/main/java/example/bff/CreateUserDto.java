package example.bff;

public record CreateUserDto(String username, String password) {
    public CreateUserDto(){
        this(null, null);
    }
}
