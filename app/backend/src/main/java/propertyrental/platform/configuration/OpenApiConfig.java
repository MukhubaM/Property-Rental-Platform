
package propertyrental.platform.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;

import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Property Rental Platform API",
                version = "1.0.0",
                description = """
                        The official REST API documentation for the Property Rental Platform.

                        This platform provides a centralized solution for discovering and listing rental accommodation across South Africa.

                        Property owners can list properties and rental units, while prospective tenants can browse available accommodation, view property details, and submit rental inquiries.

                        The API follows a modular monolith architecture and provides endpoints for identity management, property listings, location data, media management, inquiries, and listing moderation.
                        """,
                contact = @Contact(
                        name = "Property Rental Platform - Development Team",
                        url = "https://github.com/MukhubaM/Property-Rental-Platform"
                ),
                license = @License(
                        name = "Proprietary",
                        url = "https://github.com/MukhubaM/Property-Rental-Platform"
                )
        ),
        servers = {
                @Server(
                        url = "http://localhost:8080",
                        description = "Local Development Server"
                )
        }
)
public class OpenApiConfig {
}
