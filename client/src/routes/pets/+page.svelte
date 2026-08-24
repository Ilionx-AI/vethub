<script lang="ts">
	import { searchPets } from '$lib/api/pet/PetController';
	import { getOwners } from '$lib/api/owner/OwnerController';
	import type { PetResponse, OwnerResponse } from '$lib/api/models';
	import { Button } from '$lib/components/ui/button';
	import { Input } from '$lib/components/ui/input';
	import { Badge } from '$lib/components/ui/badge';
	import * as Table from '$lib/components/ui/table';
	import { PawPrint, Search, Loader2, AlertCircle } from 'lucide-svelte';
	import { toast } from 'svelte-sonner';
	import {
		daysSinceVaccine,
		getVaccineStatusColor,
		getVaccineStatusBadgeVariant,
		getVaccineStatusLabel
	} from '$lib/utils/healthMetrics';

	let pets = $state<PetResponse[]>([]);
	let owners = $state<OwnerResponse[]>([]);
	let loading = $state(true);
	let searchQuery = $state('');

	// Get owner name by ID
	let ownerMap = $derived(() => {
		const map = new Map<number, string>();
		owners.forEach((owner) => {
			map.set(owner.id!, `${owner.firstName} ${owner.lastName}`);
		});
		return map;
	});

	// Format birth date
	function formatDate(dateStr: string | undefined): string {
		if (!dateStr) return '-';
		return new Date(dateStr).toLocaleDateString('en-US', {
			year: 'numeric',
			month: '2-digit',
			day: '2-digit'
		});
	}

	async function loadData() {
		loading = true;
		try {
			const [petsData, ownersData] = await Promise.all([
				searchPets(searchQuery),
				getOwners()
			]);
			pets = petsData;
			owners = ownersData;
		} catch (err) {
			toast.error('Failed to load pets');
			console.error('Error loading pets:', err);
		} finally {
			loading = false;
		}
	}

	// Load data when search query changes
	$effect(() => {
		loadData();
	});
</script>

<svelte:head>
	<title>Pets | VetHub</title>
</svelte:head>

<div class="container mx-auto px-4 py-8">
	<!-- Header -->
	<div class="mb-8 flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
		<div class="flex items-center gap-3">
			<div class="flex h-12 w-12 items-center justify-center rounded-lg bg-primary/10">
				<PawPrint class="h-6 w-6 text-primary" />
			</div>
			<div>
				<h1 class="text-2xl font-bold text-foreground">Pets</h1>
				<p class="text-sm text-muted-foreground">View and manage patients</p>
			</div>
		</div>
		<Button href="/pets/new" variant="default">
			Add New Pet
		</Button>
	</div>

	<!-- Search -->
	<div class="mb-6">
		<div class="relative max-w-md">
			<Search class="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
			<Input
				type="search"
				placeholder="Search by name or type..."
				bind:value={searchQuery}
				class="pl-10"
			/>
		</div>
	</div>

	<!-- Table -->
	{#if loading}
		<div class="card p-12 text-center">
			<div class="mx-auto mb-4 h-8 w-8 animate-spin rounded-full border-4 border-primary border-t-transparent"></div>
			<p class="text-muted-foreground">Loading pets...</p>
		</div>
	{:else if pets.length === 0}
		<div class="card p-12 text-center">
			<PawPrint class="mx-auto mb-4 h-12 w-12 text-muted-foreground/50" />
			{#if searchQuery}
				<p class="text-muted-foreground">No pets found matching "{searchQuery}"</p>
			{:else}
				<p class="text-muted-foreground">No pets registered yet</p>
			{/if}
		</div>
	{:else}
		<div class="card overflow-hidden">
			<Table.Root>
				<Table.Header>
					<Table.Row>
						<Table.Head>Name</Table.Head>
						<Table.Head>Type</Table.Head>
						<Table.Head>Birth Date</Table.Head>
						<Table.Head>Weight</Table.Head>
						<Table.Head class="text-center">Vaccine Status</Table.Head>
						<Table.Head>Allergies</Table.Head>
						<Table.Head>Owner</Table.Head>
						<Table.Head class="w-[100px]">Actions</Table.Head>
					</Table.Row>
				</Table.Header>
				<Table.Body>
					{#each pets as pet (pet.id)}
						<Table.Row class="hover:bg-muted/50">
							<Table.Cell>
								<span class="font-medium text-foreground">{pet.name}</span>
							</Table.Cell>
							<Table.Cell>
								<span class="text-sm text-muted-foreground">{pet.type?.name ?? '-'}</span>
							</Table.Cell>
							<Table.Cell>
								<span class="text-sm text-muted-foreground">{formatDate(pet.birthDate)}</span>
							</Table.Cell>
							<Table.Cell>
								<span class="text-sm text-muted-foreground">
									{pet.weight ? `${pet.weight} kg` : 'N/A'}
								</span>
							</Table.Cell>
							<Table.Cell class="text-center">
								{#if pet.lastVaccineDate}
									<div class="flex justify-center">
										<Badge variant={getVaccineStatusBadgeVariant(daysSinceVaccine(pet.lastVaccineDate))}>
											{getVaccineStatusLabel(daysSinceVaccine(pet.lastVaccineDate))}
										</Badge>
									</div>
								{:else}
									<span class="text-xs text-muted-foreground">-</span>
								{/if}
							</Table.Cell>
							<Table.Cell>
								{#if pet.allergies}
									<div class="flex items-center gap-1">
										<AlertCircle class="h-4 w-4 text-red-600" />
										<span class="text-xs text-muted-foreground">Has allergies</span>
									</div>
								{:else}
									<span class="text-xs text-muted-foreground">-</span>
								{/if}
							</Table.Cell>
							<Table.Cell>
								<span class="text-sm text-muted-foreground">
									{pet.ownerId ? ownerMap().get(pet.ownerId) ?? '-' : '-'}
								</span>
							</Table.Cell>
							<Table.Cell>
								<Button
									variant="ghost"
									size="sm"
									href="/owners/{pet.ownerId}/pets/{pet.id}"
								>
									View
								</Button>
							</Table.Cell>
						</Table.Row>
					{/each}
				</Table.Body>
			</Table.Root>
		</div>
		<p class="mt-4 text-sm text-muted-foreground">
			Showing {pets.length} pets
		</p>
	{/if}
</div>
