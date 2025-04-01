/*
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.owner;

import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.validation.Valid;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.samples.petclinic.vet.VetRepository;

/**
 * @author Juergen Hoeller
 * @author Ken Krebs
 * @author Arjen Poutsma
 * @author Michael Isvy
 * @author Dave Syer
 * @author Wick Dynex
 */
@Controller
class VisitController {

	private final OwnerRepository owners;

	private final VetRepository vets;

	public VisitController(OwnerRepository owners, VetRepository vets) {
		this.owners = owners;
		this.vets = vets;
	}

	@InitBinder
	public void setAllowedFields(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields("id");
	}

	/**
	 * Called before each and every @RequestMapping annotated method. 2 goals: - Make sure
	 * we always have fresh data - Since we do not use the session scope, make sure that
	 * Pet object always has an id (Even though id is not part of the form fields)
	 * @param petId
	 * @return Pet
	 */
	@ModelAttribute("visit")
	public Visit loadPetWithVisit(@PathVariable("ownerId") int ownerId, @PathVariable("petId") int petId,
			Map<String, Object> model) {
		Optional<Owner> optionalOwner = owners.findById(ownerId);
		Owner owner = optionalOwner.orElseThrow(() -> new IllegalArgumentException(
				"Owner not found with id: " + ownerId + ". Please ensure the ID is correct "));

		Pet pet = owner.getPet(petId);
		model.put("pet", pet);
		model.put("owner", owner);

		Visit visit = new Visit();
		pet.addVisit(visit);
		return visit;
	}

	// Spring MVC calls method loadPetWithVisit(...) before initNewVisitForm is
	// called
	@GetMapping("/owners/{ownerId}/pets/{petId}/visits/new")
	public String initNewVisitForm() {
		return "pets/createOrUpdateVisitForm";
	}

	// Spring MVC calls method loadPetWithVisit(...) before processNewVisitForm is
	// called
	@PostMapping("/owners/{ownerId}/pets/{petId}/visits/new")
	public String processNewVisitForm(@ModelAttribute Owner owner, @PathVariable int petId, @Valid Visit visit,
			BindingResult result, RedirectAttributes redirectAttributes) {
		if (result.hasErrors()) {
			return "pets/createOrUpdateVisitForm";
		}

		owner.addVisit(petId, visit);
		this.owners.save(owner);
		redirectAttributes.addFlashAttribute("message", "Your visit has been booked");
		return "redirect:/owners/{ownerId}";
	}

	@GetMapping("/owners/{ownerId}/visits/report")
	public String generateVisitsReport(@PathVariable("ownerId") int ownerId) {
		Optional<Owner> optionalOwner = owners.findById(ownerId);
		Owner owner = optionalOwner
			.orElseThrow(() -> new IllegalArgumentException("Owner not found with id: " + ownerId));

		// Print owner's pets and their visits
		System.out
			.println("\n=== Visits Report for Owner: " + owner.getFirstName() + " " + owner.getLastName() + " ===");
		owner.getPets().forEach(pet -> {
			System.out.println("\nPet: " + pet.getName() + " (" + pet.getType().getName() + ")");
			if (pet.getVisits().isEmpty()) {
				System.out.println("  No visits registered");
			}
			else {
				pet.getVisits().forEach(visit -> {
					System.out.printf("  Date: %s | Description: %s%n", visit.getDate(), visit.getDescription());
				});
			}
		});

		// Print vets and their patients
		System.out.println("\n=== Veterinarians and Their Patients ===");
		vets.findAll().forEach(vet -> {
			System.out.println("\nVet: " + vet.getFirstName() + " " + vet.getLastName());

			// Get all pets attended by this vet
			List<Pet> petsAttended = owner.getPets().stream().collect(Collectors.toList());

			if (petsAttended.isEmpty()) {
				System.out.println("  No patients for this vet");
			}
			else {
				petsAttended.forEach(pet -> {
					System.out.printf("  Pet: %s (Owner: %s %s)%n", pet.getName(), owner.getFirstName(),
							owner.getLastName());
				});
			}
		});

		return "redirect:/owners/" + ownerId;
	}

}
