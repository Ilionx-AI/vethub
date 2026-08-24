<script lang="ts">
	import { goto } from '$app/navigation';
	import { createPet } from '$lib/api/pet/PetController';
	import { getPetTypes } from '$lib/api/pet-type/PetTypeController';
	import type { PetTypeResponse } from '$lib/api/models';
	import PetForm from '$lib/components/pets/PetForm.svelte';
	import { Button } from '$lib/components/ui/button';
	import { ArrowLeft, PawPrint } from 'lucide-svelte';
	import { toast } from 'svelte-sonner';

	let petTypes = $state<PetTypeResponse[]>([]);
	let loading = $state(true);

	async function loadPetTypes() {
		loading = true;
		try {
			petTypes = await getPetTypes();
		} catch (err) {
			toast.error('Failed to load pet types');
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
			const pet = await createPet({
				name: data.name,
				birthDate: data.birthDate,
				typeId: data.typeId,
				ownerId: 1, // Required for global endpoint - can be updated later via owner page
				weight: data.weight ?? undefined,
				lastVaccineDate: data.lastVaccineDate ?? undefined,
				allergies: data.allergies ?? undefined,
				dietaryRestrictions: data.dietaryRestrictions ?? undefined,
				medicalNotes: data.medicalNotes ?? undefined
			});
			toast.success('Pet created successfully');
			goto('/pets');
		} catch (err) {
			toast.error('Failed to create pet');
			console.error('Error:', err);
		}
	}

	// Load pet types on mount
	$effect(() => {
		loadPetTypes();
	});
</script>

<svelte:head>
	<title>Create New Pet | VetHub</title>
</svelte:head>

<div class="container mx-auto max-w-2xl px-4 py-8">
	<!-- Back button -->
	<Button variant="ghost" href="/pets" class="mb-6 gap-2">
		<ArrowLeft class="h-4 w-4 flex-shrink-0" />
		<span>Back to Pets</span>
	</Button>

	<div class="mb-6 space-y-1">
		<h1 class="text-2xl sm:text-3xl font-bold">Create New Pet</h1>
		<p class="text-muted-foreground text-sm sm:text-base">Register a new pet in the system</p>
	</div>

	{#if loading}
		<div class="card p-12 text-center">
			<div class="mx-auto mb-4 h-8 w-8 animate-spin rounded-full border-4 border-primary border-t-transparent"></div>
			<p class="text-muted-foreground">Loading pet types...</p>
		</div>
	{:else if petTypes.length === 0}
		<div class="card p-12 text-center">
			<PawPrint class="mx-auto mb-4 h-12 w-12 text-muted-foreground/50" />
			<p class="text-muted-foreground">No pet types available. Please add pet types first.</p>
		</div>
	{:else}
		<div class="card p-4 sm:p-6">
			<PetForm
				{petTypes}
				onSubmit={handleSubmit}
				submitLabel="Create Pet"
			/>
		</div>
	{/if}
</div>
