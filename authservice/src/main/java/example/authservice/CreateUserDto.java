package example.authservice;

public record CreateUserDto(String username, String password) {
    public CreateUserDto(){
        this(null, null);
    }
}
