<script lang="ts">
	import type { components } from '$lib/types/api';
	import { Button } from '$lib/components/ui/button';
	import { Input } from '$lib/components/ui/input';
	import { Label } from '$lib/components/ui/label';
	import { Textarea } from '$lib/components/ui/textarea';
	import * as Select from '$lib/components/ui/select';
	import { Badge } from '$lib/components/ui/badge';
	import { Loader2 } from 'lucide-svelte';

	type Vet = components['schemas']['VetResponse'];
	type Specialty = components['schemas']['SpecialtyResponse'];

	interface Props {
		visitDate?: string;
		description?: string;
		medicalNotes?: string;
		vetId?: number;
		vets: Vet[];
		onSubmit: (data: { visitDate: string; description: string; medicalNotes?: string; vetId?: number }) => Promise<void>;
		submitLabel?: string;
	}

	let {
		visitDate: initialVisitDate = '',
		description: initialDescription = '',
		medicalNotes: initialMedicalNotes = '',
		vetId: initialVetId,
		vets,
		onSubmit,
		submitLabel = 'Save'
	}: Props = $props();

	let visitDate = $state(initialVisitDate || getToday());
	let description = $state(initialDescription);
	let medicalNotes = $state(initialMedicalNotes);
	let selectedVetId = $state<number | undefined>(initialVetId);
	let submitting = $state(false);

	// Get selected vet for display
	let selectedVet = $derived(vets.find((v) => v.id === selectedVetId));

	function getToday(): string {
		const today = new Date();
		return today.toISOString().split('T')[0];
	}

	function getSpecialtyDisplay(specialties?: Specialty[]): string {
		if (!specialties || specialties.length === 0) return 'General Practice';
		return specialties.map((s) => s.name).join(', ');
	}

	async function handleSubmit(e: Event) {
		e.preventDefault();
		submitting = true;
		try {
			await onSubmit({
				visitDate,
				description: description.trim(),
				medicalNotes: medicalNotes?.trim() || undefined,
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
			class="min-h-[100px] sm:min-h-[80px]"
		/>
	</div>

	<div class="space-y-2">
		<Label for="medicalNotes">Medical Notes (Optional)</Label>
		<Textarea
			id="medicalNotes"
			bind:value={medicalNotes}
			placeholder="Additional medical notes, observations, or follow-up instructions"
			disabled={submitting}
			class="min-h-[80px] sm:min-h-[60px]"
		/>
	</div>

	<div class="space-y-2">
		<Label for="vet">Veterinarian (Optional)</Label>
		<Select.Root 
			type="single"
			value={selectedVetId?.toString()}
			onValueChange={(value) => selectedVetId = value ? Number(value) : undefined}
		>
			<Select.Trigger id="vet" class="w-full" disabled={submitting}>
				{#if selectedVet}
					<div class="flex items-center gap-2 w-full truncate">
						<span class="truncate">{selectedVet.firstName} {selectedVet.lastName}</span>
						{#if selectedVet.specialties && selectedVet.specialties.length > 0}
							<span class="text-xs text-muted-foreground hidden sm:inline">
								({getSpecialtyDisplay(selectedVet.specialties)})
							</span>
						{/if}
					</div>
				{:else}
					<span class="text-muted-foreground">Select a veterinarian</span>
				{/if}
			</Select.Trigger>
			<Select.Content>
				{#each vets as vet (vet.id)}
					<Select.Item value={vet.id.toString()}>
						<div class="flex items-center gap-2">
							<span>{vet.firstName} {vet.lastName}</span>
							{#if vet.specialties && vet.specialties.length > 0}
								<span class="text-xs text-muted-foreground">
									({getSpecialtyDisplay(vet.specialties)})
								</span>
							{/if}
						</div>
					</Select.Item>
				{/each}
			</Select.Content>
		</Select.Root>
		<p class="text-xs text-muted-foreground">Leave blank if no specific vet is assigned</p>
	</div>

	<div class="flex flex-col-reverse sm:flex-row gap-3 sm:justify-end">
		<Button 
			type="button" 
			variant="outline" 
			onclick={() => history.back()} 
			disabled={submitting}
			class="w-full sm:w-auto"
		>
			Cancel
		</Button>
		<Button 
			type="submit" 
			disabled={submitting}
			class="w-full sm:w-auto"
		>
			{#if submitting}
				<Loader2 class="mr-2 h-4 w-4 animate-spin" />
			{/if}
			{submitLabel}
		</Button>
	</div>
</form>
