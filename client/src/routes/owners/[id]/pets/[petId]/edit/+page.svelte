<script lang="ts">
	import { page } from '$app/stores';
	import { goto } from '$app/navigation';
	import { getPetForOwner, updatePetForOwner } from '$lib/api/pet/PetController';
	import { getPetTypes } from '$lib/api/pet-type/PetTypeController';
	import type { PetResponse, PetTypeResponse, UpdatePetRequest } from '$lib/api/models';
	import PetForm from '$lib/components/pets/PetForm.svelte';
	import { Button } from '$lib/components/ui/button';
	import { ArrowLeft, PawPrint } from 'lucide-svelte';
	import { toast } from 'svelte-sonner';

	let pet = $state<PetResponse | null>(null);
	let petTypes = $state<PetTypeResponse[]>([]);
	let loading = $state(true);

	const ownerId = $derived(Number($page.params.id));
	const petId = $derived(Number($page.params.petId));

	async function loadData() {
		loading = true;
		try {
			// Load pet and pet types in parallel
			const [petData, typesData] = await Promise.all([
				getPetForOwner(ownerId, petId),
				getPetTypes()
			]);

			pet = petData;
			petTypes = typesData;
		} catch (err) {
			toast.error('Failed to load data');
			console.error('Error:', err);
		} finally {
			loading = false;
		}
	}

	async function handleSubmit(data: {
		name: string;
		birthDate: string;
		typeId: number;
		weight?: number | null;
		lastVaccineDate?: string | null;
		allergies?: string | null;
		dietaryRestrictions?: string | null;
		medicalNotes?: string | null;
	}) {
		try {
			const request: UpdatePetRequest = {
				name: data.name,
				birthDate: data.birthDate,
				typeId: data.typeId,
				weight: data.weight ?? undefined,
				lastVaccineDate: data.lastVaccineDate ?? undefined,
				allergies: data.allergies ?? undefined,
				dietaryRestrictions: data.dietaryRestrictions ?? undefined,
				medicalNotes: data.medicalNotes ?? undefined
			};

			await updatePetForOwner(ownerId, petId, request);
			toast.success('Pet updated successfully');
			goto(`/owners/${ownerId}/pets/${petId}`);
		} catch (err) {
			toast.error('Failed to update pet');
			console.error('Error:', err);
		}
	}

	// Load data on mount
	$effect(() => {
		if (ownerId && petId) {
			loadData();
		}
	});
</script>

<svelte:head>
	<title>Edit {pet ? pet.name : 'Pet'} | VetHub</title>
</svelte:head>

<div class="container mx-auto max-w-2xl px-4 py-8">
	<!-- Back button -->
	<Button variant="ghost" href="/owners/{ownerId}/pets/{petId}" class="mb-6 gap-2">
		<ArrowLeft class="h-4 w-4" />
		Back to Pet
	</Button>

	<div class="mb-6">
		<h1 class="text-2xl font-bold">Edit Pet</h1>
		<p class="text-muted-foreground">Update pet information</p>
	</div>

	{#if loading}
		<div class="card p-12 text-center">
			<div class="mx-auto mb-4 h-8 w-8 animate-spin rounded-full border-4 border-primary border-t-transparent"></div>
			<p class="text-muted-foreground">Loading pet...</p>
		</div>
	{:else if !pet}
		<div class="card p-12 text-center">
			<PawPrint class="mx-auto mb-4 h-12 w-12 text-muted-foreground/50" />
			<p class="text-muted-foreground">Pet not found</p>
		</div>
	{:else}
		<div class="card p-6">
			<PetForm
				name={pet.name}
				birthDate={pet.birthDate}
				typeId={pet.type?.id}
				weight={pet.weight}
				lastVaccineDate={pet.lastVaccineDate}
				allergies={pet.allergies}
				dietaryRestrictions={pet.dietaryRestrictions}
				medicalNotes={pet.medicalNotes}
				{petTypes}
				onSubmit={handleSubmit}
				submitLabel="Save Changes"
			/>
		</div>
	{/if}
</div>
