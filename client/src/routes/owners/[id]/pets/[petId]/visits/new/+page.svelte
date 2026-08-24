<script lang="ts">
	import { page } from '$app/stores';
	import { goto } from '$app/navigation';
	import { createVisitForPet } from '$lib/api/visit/VisitController';
	import { getVets } from '$lib/api/vet/VetController';
	import VisitForm from '$lib/components/visits/VisitForm.svelte';
	import { ArrowLeft } from 'lucide-svelte';
	import { Button } from '$lib/components/ui/button';
	import { toast } from 'svelte-sonner';

	const ownerId = $derived(Number($page.params.id));
	const petId = $derived(Number($page.params.petId));

	let vets = $state<any[]>([]);
	let loading = $state(true);

	async function loadVets() {
		try {
			vets = await getVets();
		} catch (err) {
			toast.error('Failed to load veterinarians');
			console.error('Error loading vets:', err);
		} finally {
			loading = false;
		}
	}

	loadVets();

	async function handleSubmit(data: { visitDate: string; description: string; medicalNotes?: string; vetId?: number }) {
		try {
			await createVisitForPet(ownerId, petId, {
				date: data.visitDate,
				description: data.description,
				vetId: data.vetId
			});
			toast.success('Visit recorded successfully');
			goto(`/owners/${ownerId}/pets/${petId}`);
		} catch (err) {
			toast.error('Failed to create visit');
			console.error('Error:', err);
		}
	}
</script>

<svelte:head>
	<title>Add Visit | VetHub</title>
</svelte:head>

<div class="container mx-auto max-w-2xl px-4 py-8">
	<!-- Back button -->
	<Button variant="ghost" href="/owners/{ownerId}/pets/{petId}" class="mb-6 gap-2">
		<ArrowLeft class="h-4 w-4 flex-shrink-0" />
		<span>Back to Pet</span>
	</Button>

	<div class="mb-6 space-y-2">
		<h1 class="text-2xl sm:text-3xl font-bold">Record New Visit</h1>
		<p class="text-muted-foreground text-sm sm:text-base">Add a new visit record for this pet</p>
	</div>

	<div class="card p-4 sm:p-6">
		{#if loading}
			<div class="flex items-center justify-center py-8">
				<p class="text-muted-foreground">Loading veterinarians...</p>
			</div>
		{:else}
			<VisitForm {vets} onSubmit={handleSubmit} submitLabel="Record Visit" />
		{/if}
	</div>
</div>
