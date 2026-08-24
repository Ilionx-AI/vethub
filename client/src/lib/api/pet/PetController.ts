/**
 * Pet API Controller
 *
 * Thin API layer for pet-related operations.
 * Handles HTTP communication only - no business logic.
 */
import { client } from '$lib/api/client';
import { SERVER_BASE_URL, API_USERNAME, API_PASSWORD } from '$lib/config/constants';
import type {
	PetResponse,
	CreatePetRequest,
	UpdatePetRequest
} from '$lib/api/models';

/**
 * Get all pets
 */
export async function getPets(): Promise<PetResponse[]> {
	const { data, error } = await client.GET('/v1/pets');
	if (error) throw error;
	return data ?? [];
}

/**
 * Search pets by name
 */
export async function searchPets(name: string): Promise<PetResponse[]> {
	// Build URL with query parameter since OpenAPI spec doesn't document it
	const query = name.trim() ? `?name=${encodeURIComponent(name)}` : '';
	const url = `${SERVER_BASE_URL}/v1/pets${query}`;
	
	try {
		const credentials = `${API_USERNAME}:${API_PASSWORD}`;
		const authHeader = `Basic ${btoa(credentials)}`;
		
		const response = await fetch(url, {
			method: 'GET',
			headers: {
				'Content-Type': 'application/json',
				'Authorization': authHeader
			}
		});
		
		if (!response.ok) {
			throw new Error(`Failed to search pets: ${response.statusText}`);
		}
		
		const data = await response.json();
		return Array.isArray(data) ? data : [];
	} catch (error) {
		console.error('Error searching pets:', error);
		throw error;
	}
}

/**
 * Get pet by ID
 */
export async function getPetById(id: number): Promise<PetResponse> {
	const { data, error } = await client.GET('/v1/pets/{id}', {
		params: { path: { id } }
	});
	if (error) throw error;
	if (!data) throw new Error('Pet not found');
	return data;
}

/**
 * Get pets by owner ID
 */
export async function getPetsByOwnerId(ownerId: number): Promise<PetResponse[]> {
	const { data, error } = await client.GET('/v1/owners/{ownerId}/pets', {
		params: { path: { ownerId } }
	});
	if (error) throw error;
	return data ?? [];
}

/**
 * Create a new pet
 */
export async function createPet(request: CreatePetRequest): Promise<PetResponse> {
	const { data, error } = await client.POST('/v1/pets', {
		body: request
	});
	if (error) throw error;
	if (!data) throw new Error('Failed to create pet');
	return data;
}

/**
 * Create a new pet for a specific owner
 */
export async function createPetForOwner(ownerId: number, request: Omit<CreatePetRequest, 'ownerId'>): Promise<PetResponse> {
	const { data, error } = await client.POST('/v1/owners/{ownerId}/pets', {
		params: { path: { ownerId } },
		body: { ...request, ownerId } as CreatePetRequest
	});
	if (error) throw error;
	if (!data) throw new Error('Failed to create pet');
	return data;
}


/**
 * Update an existing pet
 */
export async function updatePet(id: number, request: UpdatePetRequest): Promise<PetResponse> {
	const { data, error } = await client.PUT('/v1/pets/{id}', {
		params: { path: { id } },
		body: request
	});
	if (error) throw error;
	if (!data) throw new Error('Failed to update pet');
	return data;
}

/**
 * Delete a pet
 */
export async function deletePet(id: number): Promise<void> {
	const { error } = await client.DELETE('/v1/pets/{id}', {
		params: { path: { id } }
	});
	if (error) throw error;
}

/**
 * Get a specific pet for an owner
 */
export async function getPetForOwner(ownerId: number, petId: number): Promise<PetResponse> {
	const { data, error } = await client.GET('/v1/owners/{ownerId}/pets/{petId}', {
		params: { path: { ownerId, petId } }
	});
	if (error) throw error;
	if (!data) throw new Error('Pet not found');
	return data;
}

/**
 * Update a pet for a specific owner
 */
export async function updatePetForOwner(
	ownerId: number,
	petId: number,
	request: UpdatePetRequest
): Promise<PetResponse> {
	const { data, error } = await client.PUT('/v1/owners/{ownerId}/pets/{petId}', {
		params: { path: { ownerId, petId } },
		body: request
	});
	if (error) throw error;
	if (!data) throw new Error('Failed to update pet');
	return data;
}

/**
 * Delete a pet for a specific owner
 */
export async function deletePetForOwner(ownerId: number, petId: number): Promise<void> {
	const { error } = await client.DELETE('/v1/owners/{ownerId}/pets/{petId}', {
		params: { path: { ownerId, petId } }
	});
	if (error) throw error;
}
