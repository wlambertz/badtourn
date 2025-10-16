package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record Venue(@NotBlank String name, @Valid Address address, @Valid Capacity peopleCapacity) {
	@AssertTrue(message = "Venue capacity must use PEOPLE unit when amount is set")
	public boolean isPeopleCapacityConsistent() {
		return peopleCapacity == null
			|| peopleCapacity.amount() == null
			|| peopleCapacity.unit() == Capacity.Unit.PEOPLE;
	}

	public record Address(
		@NotBlank String streetWithNumber,
		@NotBlank @Size(min = 5, max = 5, message = "Postal code must be exactly 5 characters") String postalCode,
		@NotBlank String city
	) {
	}
}
