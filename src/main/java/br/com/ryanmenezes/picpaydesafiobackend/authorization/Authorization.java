package br.com.ryanmenezes.picpaydesafiobackend.authorization;

public record Authorization(
    String status
) {
    public boolean isAuthorized() {
        return status == "success";
    }
}
