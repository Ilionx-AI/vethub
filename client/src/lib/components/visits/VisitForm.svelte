<script lang="ts">
	import type { components } from '$lib/types/api';
	import { Button } from '$lib/components/ui/button';
	import { Input } from '$lib/components/ui/input';
	import { Label } from '$lib/components/ui/label';
	import { Textarea } from '$lib/components/ui/textarea';
	import * as Select from '$lib/components/ui/select';
	import { Loader2 } from 'lucide-svelte';

	type Vet = components['schemas']['VetResponse'];

	interface Props {
		visitDate?: string;
		description?: string;
		vetId?: number;
		vets: Vet[];
		onSubmit: (data: { visitDate: string; description: string; vetId: number }) => Promise<void>;
		submitLabel?: string;
	}

	let {
		visitDate: initialVisitDate = '',
		description: initialDescription = '',
		vetId: initialVetId,
		vets,
		onSubmit,
		submitLabel = 'Save'
	}: Props = $props();

	let visitDate = $state(initialVisitDate || getToday());
	let description = $state(initialDescription);
	let selectedVetId = $state<number | undefined>(initialVetId);
	let submitting = $state(false);

	// Get selected vet for display
	let selectedVet = $derived(vets.find((v) => v.id === selectedVetId));

	function getToday(): string {
		const today = new Date();
		return today.toISOString().split('T')[0];
	}

	async function handleSubmit(e: Event) {
		e.preventDefault();
		if (!selectedVetId) {
			return;
		}
		submitting = true;
		try {
			await onSubmit({
				visitDate,
				description: description.trim(),
				vetId: selectedVetId
			});
		} finally {
			submitting = false;
		}
	}
</script>

<form onsubmit={handleSubmit} class="space-y-6">
	<div class="space-y-2">
		<Label for="visitDate">Visit Date</Label>
		<Input
			id="visitDate"
			type="date"
			bind:value={visitDate}
			required
			disabled={submitting}
		/>
	</div>

	<div class="space-y-2">
		<Label for="description">Description</Label>
		<Textarea
			id="description"
			bind:value={description}
			placeholder="Enter visit description (e.g., Rabies shot, Annual checkup)"
			required
			disabled={submitting}
			rows={4}
		/>
	</div>

	<div class="space-y-2">
		<Label for="vet">Veterinarian</Label>
		<Select.Root 
			type="single"
			value={selectedVetId?.toString()}
			onValueChange={(value) => selectedVetId = value ? Number(value) : undefined}
		>
			<Select.Trigger id="vet" class="w-full" disabled={submitting}>
				{#if selectedVet}
					{selectedVet.firstName} {selectedVet.lastName}
				{:else}
					Select a veterinarian
				{/if}
			</Select.Trigger>
			<Select.Content>
				{#each vets as vet (vet.id)}
					<Select.Item value={vet.id.toString()}>
						{vet.firstName} {vet.lastName}
					</Select.Item>
				{/each}
			</Select.Content>
		</Select.Root>
	</div>

	<div class="flex justify-end gap-3">
		<Button type="button" variant="outline" onclick={() => history.back()} disabled={submitting}>
			Cancel
		</Button>
		<Button type="submit" disabled={submitting || !selectedVetId}>
			{#if submitting}
				<Loader2 class="mr-2 h-4 w-4 animate-spin" />
			{/if}
			{submitLabel}
		</Button>
	</div>
</form>
