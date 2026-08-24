<script lang="ts">
	import { getVisits } from '$lib/api/visit/VisitController';
	import { getPetById } from '$lib/api/pet/PetController';
	import type { VisitResponse, PetResponse } from '$lib/api/models';
	import { Button } from '$lib/components/ui/button';
	import { Input } from '$lib/components/ui/input';
	import * as Table from '$lib/components/ui/table';
	import { PetDetailModal } from '$lib/components/pets';
	import { Calendar, Search, Loader2, ExternalLink, Stethoscope } from 'lucide-svelte';
	import { toast } from 'svelte-sonner';

	let visits = $state<VisitResponse[]>([]);
	let petCache = $state<Record<number, PetResponse>>({});
	let loading = $state(true);
	let searchQuery = $state('');
	let selectedPetId = $state<number | null>(null);
	let isModalOpen = $state(false);

	const filteredVisits = $derived(
		visits.filter(
			(visit) =>
				visit.description.toLowerCase().includes(searchQuery.toLowerCase()) ||
				visit.date.includes(searchQuery)
		)
	);

	async function loadVisits() {
		loading = true;
		try {
			visits = await getVisits();
		} catch (e) {
			toast.error('Failed to load visits');
			console.error('Error loading visits:', e);
		} finally {
			loading = false;
		}
	}

	async function getPetNameForVisit(petId: number): Promise<string> {
		// Check cache first
		if (petCache[petId]) {
			return petCache[petId].name;
		}

		try {
			const pet = await getPetById(petId);
			petCache[petId] = pet;
			return pet.name;
		} catch (e) {
			console.error('Error loading pet:', e);
			return `Pet #${petId}`;
		}
	}

	// Pre-load pet names for all visits
	async function loadPetNames() {
		const petIds = [...new Set(visits.map((v) => v.petId))];
		for (const petId of petIds) {
			await getPetNameForVisit(petId);
		}
	}

	function formatDate(dateStr: string): string {
		const date = new Date(dateStr);
		return date.toLocaleDateString('en-US', {
			year: 'numeric',
			month: 'short',
			day: 'numeric'
		});
	}

	function openPetDetails(petId: number) {
		selectedPetId = petId;
		isModalOpen = true;
	}

	function closePetDetails() {
		isModalOpen = false;
		selectedPetId = null;
	}

	$effect(() => {
		loadVisits();
	});

	$effect(() => {
		if (visits.length > 0) {
			loadPetNames();
		}
	});
</script>

<svelte:head>
	<title>Visits | VetHub</title>
</svelte:head>

<div class="container mx-auto px-4 py-8">
	<!-- Header -->
	<div class="mb-8">
		<div class="flex items-center gap-3 mb-2">
			<div class="flex h-10 w-10 items-center justify-center rounded-lg bg-primary/10 flex-shrink-0">
				<Calendar class="h-5 w-5 text-primary" />
			</div>
			<h1 class="text-3xl font-bold text-foreground">Visits</h1>
		</div>
		<p class="text-muted-foreground">View all veterinary visits across all pets</p>
	</div>

	<!-- Search -->
	<div class="mb-6">
		<div class="relative w-full sm:max-w-md">
			<Search class="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground flex-shrink-0" />
			<Input
				type="text"
				placeholder="Search by description or date..."
				class="pl-10"
				bind:value={searchQuery}
			/>
		</div>
	</div>

	<!-- Content -->
	{#if loading}
		<div class="flex items-center justify-center py-12">
			<Loader2 class="h-8 w-8 animate-spin text-primary" />
		</div>
	{:else if visits.length === 0}
		<div class="rounded-lg border border-dashed p-12 text-center">
			<Calendar class="mx-auto h-12 w-12 text-muted-foreground/50" />
			<h3 class="mt-4 text-lg font-medium text-foreground">No visits yet</h3>
			<p class="mt-2 text-sm text-muted-foreground">
				Visits will appear here once they are recorded for pets.
			</p>
		</div>
	{:else}
		<!-- Desktop Table View (md and up) -->
		<div class="hidden md:block rounded-lg border bg-card overflow-x-auto">
			<Table.Root>
				<Table.Header>
					<Table.Row>
						<Table.Head class="w-[120px] sm:w-auto">Date</Table.Head>
						<Table.Head>Pet Name</Table.Head>
						<Table.Head>Description</Table.Head>
						<Table.Head class="w-[80px] text-center">Actions</Table.Head>
					</Table.Row>
				</Table.Header>
				<Table.Body>
					{#each filteredVisits as visit (visit.id)}
						<Table.Row class="hover:bg-muted/50">
							<Table.Cell class="font-medium text-sm">
								{formatDate(visit.date)}
							</Table.Cell>
							<Table.Cell class="text-sm">
								{petCache[visit.petId]?.name || `Pet #${visit.petId}`}
							</Table.Cell>
							<Table.Cell class="text-sm">
								<span class="line-clamp-2">{visit.description}</span>
							</Table.Cell>
							<Table.Cell class="text-center">
								<Button
									variant="ghost"
									size="sm"
									title="View pet details"
									onclick={() => openPetDetails(visit.petId)}
									class="hover:bg-accent"
								>
									<ExternalLink class="h-4 w-4" />
								</Button>
							</Table.Cell>
						</Table.Row>
					{:else}
						<Table.Row>
							<Table.Cell colspan={4} class="text-center text-muted-foreground py-8">
								No visits match your search
							</Table.Cell>
						</Table.Row>
					{/each}
				</Table.Body>
			</Table.Root>
		</div>

		<!-- Mobile Card View (below md) -->
		<div class="md:hidden space-y-4">
			{#each filteredVisits as visit (visit.id)}
				<div class="rounded-lg border bg-card p-4 space-y-3">
					<div class="flex items-start justify-between gap-3">
						<div class="flex-1 min-w-0">
							<div class="flex items-center gap-2 mb-2">
								<Stethoscope class="h-4 w-4 text-muted-foreground flex-shrink-0" />
								<p class="font-medium text-sm text-foreground">
									{petCache[visit.petId]?.name || `Pet #${visit.petId}`}
								</p>
							</div>
							<p class="text-xs text-muted-foreground">
								{formatDate(visit.date)}
							</p>
						</div>
						<Button
							variant="ghost"
							size="sm"
							title="View pet details"
							onclick={() => openPetDetails(visit.petId)}
							class="hover:bg-accent flex-shrink-0"
						>
							<ExternalLink class="h-4 w-4" />
						</Button>
					</div>
					<p class="text-sm text-foreground break-words">
						{visit.description}
					</p>
				</div>
			{:else}
				<div class="rounded-lg border border-dashed p-8 text-center">
					<p class="text-sm text-muted-foreground">No visits match your search</p>
				</div>
			{/each}
		</div>

		<p class="mt-4 text-sm text-muted-foreground">
			Showing {filteredVisits.length} of {visits.length} visits
		</p>
	{/if}
</div>

<!-- Pet Detail Modal -->
<PetDetailModal
	isOpen={isModalOpen}
	petId={selectedPetId}
	onClose={closePetDetails}
/>
