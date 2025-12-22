package edu.booking.hotel_booking.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI hotelBookingOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Hotel Booking API")
                        .description("Simple hotel room booking system")
                        .version("1.0.0")
                );
    }
}
