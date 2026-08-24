<script lang="ts">
	import { page } from '$app/stores';
	import { goto } from '$app/navigation';
	import { getPetForOwner, deletePetForOwner } from '$lib/api/pet/PetController';
	import type { PetResponse } from '$lib/api/models';
	import { Button } from '$lib/components/ui/button';
	import { Badge } from '$lib/components/ui/badge';
	import * as Card from '$lib/components/ui/card';
	import * as Dialog from '$lib/components/ui/dialog';
	import {
		PawPrint,
		Calendar,
		ArrowLeft,
		Pencil,
		Trash2,
		Plus,
		Stethoscope,
		AlertCircle,
		Droplet,
		Utensils,
		User
	} from 'lucide-svelte';
	import { toast } from 'svelte-sonner';
	import {
		daysSinceVaccine,
		getVaccineStatusBadgeVariant,
		getVaccineStatusLabel,
		formatVaccineDate,
		formatAge
	} from '$lib/utils/healthMetrics';

	let pet = $state<PetResponse | null>(null);
	let loading = $state(true);
	let deleteDialogOpen = $state(false);
	let deleting = $state(false);

	const ownerId = $derived(Number($page.params.id));
	const petId = $derived(Number($page.params.petId));

	async function loadPet() {
		loading = true;
		try {
			pet = await getPetForOwner(ownerId, petId);
		} catch (err) {
			toast.error('Failed to load pet');
			console.error('Error:', err);
		} finally {
			loading = false;
		}
	}

	async function deletePet() {
		deleting = true;
		try {
			await deletePetForOwner(ownerId, petId);
			toast.success('Pet deleted successfully');
			goto(`/owners/${ownerId}`);
		} catch (err) {
			toast.error('Failed to delete pet');
			console.error('Error:', err);
		} finally {
			deleting = false;
			deleteDialogOpen = false;
		}
	}

	function formatDate(dateStr: string | undefined): string {
		if (!dateStr) return 'Unknown';
		return new Date(dateStr).toLocaleDateString('en-US', {
			year: 'numeric',
			month: 'long',
			day: 'numeric'
		});
	}

	// Load pet on mount and when IDs change
	$effect(() => {
		if (ownerId && petId) {
			loadPet();
		}
	});
</script>

<svelte:head>
	<title>{pet ? pet.name : 'Pet'} | VetHub</title>
</svelte:head>

<div class="container mx-auto px-4 py-8">
	<!-- Back Button -->
	<div class="mb-6">
		<Button variant="ghost" href="/owners/{ownerId}" class="gap-2">
			<ArrowLeft class="h-4 w-4" />
			Back to Owner
		</Button>
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
		<!-- Pet Header Card -->
		<Card.Root class="mb-8">
			<Card.Header>
				<div class="flex flex-col gap-4 sm:flex-row sm:items-start sm:justify-between">
					<div class="flex items-center gap-4">
						<div class="flex h-16 w-16 items-center justify-center rounded-full bg-accent/10 flex-shrink-0">
							<PawPrint class="h-8 w-8 text-accent" />
						</div>
						<div class="min-w-0 flex-1">
							<Card.Title class="text-2xl break-words">{pet.name}</Card.Title>
							<div class="flex flex-wrap items-center gap-2 mt-1">
								<Badge variant="secondary">{pet.type?.name ?? 'Unknown type'}</Badge>
								<span class="text-muted-foreground">•</span>
								<span class="text-muted-foreground text-sm">{formatAge(pet.birthDate)}</span>
							</div>
						</div>
					</div>
					<div class="flex flex-col sm:flex-row gap-2 w-full sm:w-auto">
						<Button 
							variant="outline" 
							href="/owners/{ownerId}/pets/{petId}/edit" 
							class="gap-2 w-full sm:w-auto justify-center sm:justify-start"
						>
							<Pencil class="h-4 w-4 flex-shrink-0" />
							<span>Edit</span>
						</Button>
						<Button 
							variant="destructive" 
							onclick={() => (deleteDialogOpen = true)} 
							class="gap-2 w-full sm:w-auto justify-center sm:justify-start"
						>
							<Trash2 class="h-4 w-4 flex-shrink-0" />
							<span>Delete</span>
						</Button>
					</div>
				</div>
			</Card.Header>
			<Card.Content>
				<div class="flex items-center gap-3 text-muted-foreground text-sm">
					<Calendar class="h-5 w-5 flex-shrink-0" />
					<span>Born: {formatDate(pet.birthDate)}</span>
				</div>
			</Card.Content>
		</Card.Root>

		<!-- Health Metrics Grid -->
		<div class="mb-8 grid gap-4 sm:grid-cols-2 md:grid-cols-3">
			<!-- Weight Card -->
			{#if pet.weight}
				<Card.Root>
					<Card.Header class="pb-3">
						<div class="flex items-center gap-2">
							<Droplet class="h-5 w-5 text-blue-600 flex-shrink-0" />
							<Card.Title class="text-base">Weight</Card.Title>
						</div>
					</Card.Header>
					<Card.Content>
						<div class="text-sm text-muted-foreground">{pet.weight} kg</div>
					</Card.Content>
				</Card.Root>
			{/if}

			<!-- Last Vaccine Card -->
			{#if pet.lastVaccineDate}
				<Card.Root>
					<Card.Header class="pb-3">
						<div class="flex items-center gap-2">
							<Stethoscope class="h-5 w-5 text-green-600 flex-shrink-0" />
							<Card.Title class="text-base">Last Vaccine</Card.Title>
						</div>
					</Card.Header>
					<Card.Content>
						<div class="space-y-2">
							<div class="text-sm text-muted-foreground">{formatVaccineDate(pet.lastVaccineDate)}</div>
							<Badge variant={getVaccineStatusBadgeVariant(daysSinceVaccine(pet.lastVaccineDate))}>
								{getVaccineStatusLabel(daysSinceVaccine(pet.lastVaccineDate))}
							</Badge>
						</div>
					</Card.Content>
				</Card.Root>
			{/if}

			<!-- Allergies Card -->
			{#if pet.allergies}
				<Card.Root class="sm:col-span-2 md:col-span-3">
					<Card.Header class="pb-3">
						<div class="flex items-center gap-2">
							<AlertCircle class="h-5 w-5 text-red-600 flex-shrink-0" />
							<Card.Title class="text-base">Allergies</Card.Title>
						</div>
					</Card.Header>
					<Card.Content>
						<div class="text-sm text-foreground whitespace-pre-wrap">{pet.allergies}</div>
					</Card.Content>
				</Card.Root>
			{/if}

			<!-- Dietary Restrictions Card -->
			{#if pet.dietaryRestrictions}
				<Card.Root class="sm:col-span-2 md:col-span-3">
					<Card.Header class="pb-3">
						<div class="flex items-center gap-2">
							<Utensils class="h-5 w-5 text-orange-600 flex-shrink-0" />
							<Card.Title class="text-base">Dietary Restrictions</Card.Title>
						</div>
					</Card.Header>
					<Card.Content>
						<div class="text-sm text-foreground whitespace-pre-wrap">{pet.dietaryRestrictions}</div>
					</Card.Content>
				</Card.Root>
			{/if}

			<!-- Medical Notes Card -->
			{#if pet.medicalNotes}
				<Card.Root class="sm:col-span-2 md:col-span-3">
					<Card.Header class="pb-3">
						<div class="flex items-center gap-2">
							<AlertCircle class="h-5 w-5 text-yellow-600 flex-shrink-0" />
							<Card.Title class="text-base">Medical Notes</Card.Title>
						</div>
					</Card.Header>
					<Card.Content>
						<div class="text-sm text-foreground whitespace-pre-wrap">{pet.medicalNotes}</div>
					</Card.Content>
				</Card.Root>
			{/if}
		</div>

		<!-- Visits Section -->
		<div class="mb-6 flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
			<h2 class="text-xl font-semibold text-foreground">Visit History</h2>
			<Button href="/owners/{ownerId}/pets/{petId}/visits/new" class="gap-2 w-full sm:w-auto">
				<Plus class="h-4 w-4 flex-shrink-0" />
				Add Visit
			</Button>
		</div>

		{#if !pet.visits?.length}
			<div class="card p-8 text-center">
				<Stethoscope class="mx-auto mb-4 h-12 w-12 text-muted-foreground/50" />
				<p class="text-muted-foreground">No visits recorded for this pet</p>
				<Button href="/owners/{ownerId}/pets/{petId}/visits/new" class="mt-4 gap-2">
					<Plus class="h-4 w-4" />
					Record First Visit
				</Button>
			</div>
		{:else}
			<div class="space-y-4">
				{#each pet.visits.sort((a, b) => new Date(b.date).getTime() - new Date(a.date).getTime()) as visit (visit.id)}
					<Card.Root>
						<Card.Content class="pt-6">
							<div class="space-y-3">
								<div class="flex flex-col sm:flex-row sm:items-start sm:justify-between gap-3">
									<div class="flex items-start gap-3 sm:gap-4 flex-1 min-w-0">
										<div class="flex h-10 w-10 items-center justify-center rounded-full bg-success/10 flex-shrink-0">
											<Stethoscope class="h-5 w-5 text-success" />
										</div>
										<div class="flex-1 min-w-0">
											<p class="font-medium text-foreground break-words">{visit.description}</p>
											<p class="text-sm text-muted-foreground">{formatDate(visit.date)}</p>
										</div>
									</div>
								</div>
								
								<!-- Vet Information -->
								{#if visit.vet}
									<div class="flex items-start gap-2 sm:gap-3 bg-secondary/30 rounded-lg p-3 ml-0 sm:ml-14">
										<User class="h-4 w-4 text-muted-foreground flex-shrink-0 mt-0.5" />
										<div class="flex-1 min-w-0">
											<p class="text-sm font-medium text-foreground">
												Dr. {visit.vet.firstName} {visit.vet.lastName}
											</p>
											{#if visit.vet.specialties && visit.vet.specialties.length > 0}
												<div class="flex flex-wrap gap-1 mt-1">
													{#each visit.vet.specialties as specialty (specialty.id)}
														<Badge variant="outline" class="text-xs">
															{specialty.name}
														</Badge>
													{/each}
												</div>
											{:else}
												<p class="text-xs text-muted-foreground">General Practice</p>
											{/if}
										</div>
									</div>
								{:else}
									<div class="flex items-center gap-2 bg-secondary/30 rounded-lg p-3 ml-0 sm:ml-14">
										<User class="h-4 w-4 text-muted-foreground flex-shrink-0" />
										<p class="text-sm text-muted-foreground italic">No veterinarian assigned</p>
									</div>
								{/if}
							</div>
						</Card.Content>
					</Card.Root>
				{/each}
			</div>
		{/if}
	{/if}
</div>

<!-- Delete Confirmation Dialog -->
<Dialog.Root bind:open={deleteDialogOpen}>
	<Dialog.Content>
		<Dialog.Header>
			<Dialog.Title>Delete Pet</Dialog.Title>
			<Dialog.Description>
				Are you sure you want to delete {pet?.name}? This will also delete all visit records. This action cannot be undone.
			</Dialog.Description>
		</Dialog.Header>
		<Dialog.Footer class="flex-col-reverse sm:flex-row">
			<Button variant="outline" onclick={() => (deleteDialogOpen = false)} disabled={deleting} class="w-full sm:w-auto">
				Cancel
			</Button>
			<Button variant="destructive" onclick={deletePet} disabled={deleting} class="w-full sm:w-auto">
				{#if deleting}
					Deleting...
				{:else}
					Delete
				{/if}
			</Button>
		</Dialog.Footer>
	</Dialog.Content>
</Dialog.Root>
