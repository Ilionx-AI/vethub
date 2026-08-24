<script lang="ts">
	import { getPetById } from '$lib/api/pet/PetController';
	import type { PetResponse } from '$lib/api/models';
	import * as Dialog from '$lib/components/ui/dialog';
	import { Button } from '$lib/components/ui/button';
	import { Badge } from '$lib/components/ui/badge';
	import { Loader2, AlertCircle, Calendar, User } from 'lucide-svelte';
	import { toast } from 'svelte-sonner';

	interface Props {
		isOpen: boolean;
		petId: number | null;
		onClose: () => void;
	}

	let { isOpen = $bindable(false), petId, onClose }: Props = $props();

	let pet = $state<PetResponse | null>(null);
	let loading = $state(false);
	let error = $state<string | null>(null);

	// Calculate age from birth date
	function calculateAge(birthDate: string): string {
		const today = new Date();
		const birth = new Date(birthDate);
		let age = today.getFullYear() - birth.getFullYear();
		const monthDiff = today.getMonth() - birth.getMonth();

		if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birth.getDate())) {
			age--;
		}

		return age > 0 ? `${age} years old` : 'Less than 1 year old';
	}

	// Format date as "Aug 24, 2026"
	function formatDate(dateStr: string): string {
		const date = new Date(dateStr);
		return date.toLocaleDateString('en-US', {
			year: 'numeric',
			month: 'short',
			day: 'numeric'
		});
	}

	// Load pet data when modal opens
	async function loadPet() {
		if (!petId) return;

		loading = true;
		error = null;
		pet = null;

		try {
			pet = await getPetById(petId);
		} catch (e) {
			error = e instanceof Error ? e.message : 'Failed to load pet details';
			console.error('Error loading pet:', e);
		} finally {
			loading = false;
		}
	}

	// Watch for modal open state and petId changes
	$effect(() => {
		if (isOpen && petId) {
			loadPet();
		} else if (!isOpen) {
			// Reset state when modal closes
			pet = null;
			error = null;
		}
	});

	function handleClose() {
		isOpen = false;
		onClose();
	}
</script>

<Dialog.Root open={isOpen} onOpenChange={(newOpen) => {
	isOpen = newOpen;
	if (!newOpen) {
		onClose();
	}
}}>
	<Dialog.Content class="max-w-full sm:max-w-md max-h-[90vh] overflow-y-auto p-4 sm:p-6">
		{#if loading}
			<!-- Loading State -->
			<div class="flex items-center justify-center py-12">
				<Loader2 class="h-8 w-8 animate-spin text-primary" />
			</div>
		{:else if error}
			<!-- Error State -->
			<div class="flex flex-col items-center justify-center py-8">
				<div class="rounded-lg bg-destructive/10 p-4 text-center">
					<AlertCircle class="mx-auto h-8 w-8 text-destructive mb-2" />
					<h3 class="font-semibold text-foreground">Failed to Load Pet</h3>
					<p class="mt-2 text-sm text-muted-foreground">{error}</p>
				</div>
				<Button variant="outline" class="mt-4" onclick={handleClose}>
					Close
				</Button>
			</div>
		{:else if pet}
			<!-- Pet Details -->
			<Dialog.Header>
				<div class="flex items-start gap-3 w-full">
					<div class="flex-1 min-w-0">
						<Dialog.Title class="break-words">{pet.name}</Dialog.Title>
						{#if pet.type}
							<Badge variant="secondary" class="mt-2 inline-block">
								{pet.type.name}
							</Badge>
						{/if}
					</div>
				</div>
			</Dialog.Header>

			<div class="space-y-6">
				<!-- Basic Info Section -->
				<div class="space-y-4 border-t pt-4">
					<h3 class="font-semibold text-foreground text-sm">Basic Information</h3>

					<div class="grid grid-cols-1 sm:grid-cols-2 gap-3 sm:gap-4">
						<div class="space-y-1">
							<p class="text-xs font-medium text-muted-foreground uppercase tracking-wide">Name</p>
							<p class="text-sm font-medium text-foreground break-words">{pet.name}</p>
						</div>

						{#if pet.type}
							<div class="space-y-1">
								<p class="text-xs font-medium text-muted-foreground uppercase tracking-wide">Pet Type</p>
								<p class="text-sm font-medium text-foreground">{pet.type.name}</p>
							</div>
						{/if}

						<div class="space-y-1">
							<p class="text-xs font-medium text-muted-foreground uppercase tracking-wide">Birth Date</p>
							<p class="text-sm font-medium text-foreground">
								{formatDate(pet.birthDate)}
							</p>
						</div>

						<div class="space-y-1">
							<p class="text-xs font-medium text-muted-foreground uppercase tracking-wide">Age</p>
							<p class="text-sm font-medium text-foreground">
								{calculateAge(pet.birthDate)}
							</p>
						</div>
					</div>

					<div class="space-y-1 pt-2">
						<p class="text-xs font-medium text-muted-foreground uppercase tracking-wide">Owner ID</p>
						<p class="text-sm font-medium text-foreground">#{pet.ownerId}</p>
					</div>
				</div>

				<!-- Visits Section -->
				{#if pet.visits && pet.visits.length > 0}
					<div class="space-y-3 border-t pt-4">
						<h3 class="font-semibold text-foreground text-sm">Recent Visits</h3>

						<div class="space-y-2 max-h-[200px] overflow-y-auto">
							{#each pet.visits as visit (visit.id)}
								<div class="rounded-lg bg-muted/50 p-3 space-y-1">
									<div class="flex items-start justify-between gap-2">
										<div class="flex-1 min-w-0">
											<p class="text-sm font-medium text-foreground break-words">
												{formatDate(visit.date)}
											</p>
											<p class="mt-1 text-xs text-muted-foreground line-clamp-2">
												{visit.description}
											</p>
										</div>
									</div>
								</div>
							{/each}
						</div>
					</div>
				{:else}
					<div class="rounded-lg bg-muted/30 p-4 text-center border-t">
						<p class="text-sm text-muted-foreground">No visits recorded yet</p>
					</div>
				{/if}
			</div>

			<!-- Footer -->
			<Dialog.Footer class="mt-6 flex-col-reverse sm:flex-row">
				<Button variant="outline" onclick={handleClose} class="w-full sm:w-auto">
					Close
				</Button>
			</Dialog.Footer>
		{:else}
			<!-- Empty State -->
			<div class="flex items-center justify-center py-12">
				<p class="text-muted-foreground">No pet data available</p>
			</div>
		{/if}
	</Dialog.Content>
</Dialog.Root>
