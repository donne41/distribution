package example.service1.users;

public record CreateUserDto(String username, String password) {
    public CreateUserDto(){
        this(null, null);
    }
}
