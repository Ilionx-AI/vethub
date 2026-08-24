<script lang="ts">
	import type { components } from '$lib/types/api';
	import { Button } from '$lib/components/ui/button';
	import { Input } from '$lib/components/ui/input';
	import { Label } from '$lib/components/ui/label';
	import { Textarea } from '$lib/components/ui/textarea';
	import * as Select from '$lib/components/ui/select';
	import * as Card from '$lib/components/ui/card';
	import * as Collapsible from '$lib/components/ui/collapsible';
	import { Loader2, ChevronDown } from 'lucide-svelte';

	type PetType = components['schemas']['PetTypeResponse'];

	interface Props {
		name?: string;
		birthDate?: string;
		typeId?: number;
		weight?: number | null;
		lastVaccineDate?: string | null;
		allergies?: string | null;
		dietaryRestrictions?: string | null;
		medicalNotes?: string | null;
		petTypes: PetType[];
		onSubmit: (data: {
			name: string;
			birthDate: string;
			typeId: number;
			weight?: number | null;
			lastVaccineDate?: string | null;
			allergies?: string | null;
			dietaryRestrictions?: string | null;
			medicalNotes?: string | null;
		}) => Promise<void>;
		submitLabel?: string;
	}

	let {
		name: initialName = '',
		birthDate: initialBirthDate = '',
		typeId: initialTypeId,
		weight: initialWeight = null,
		lastVaccineDate: initialLastVaccineDate = null,
		allergies: initialAllergies = null,
		dietaryRestrictions: initialDietaryRestrictions = null,
		medicalNotes: initialMedicalNotes = null,
		petTypes,
		onSubmit,
		submitLabel = 'Save'
	}: Props = $props();

	let name = $state(initialName);
	let birthDate = $state(initialBirthDate);
	let selectedTypeId = $state<number | undefined>(initialTypeId);
	let weight = $state<string>(initialWeight ? String(initialWeight) : '');
	let lastVaccineDate = $state<string>(initialLastVaccineDate ?? '');
	let allergies = $state<string>(initialAllergies ?? '');
	let dietaryRestrictions = $state<string>(initialDietaryRestrictions ?? '');
	let medicalNotes = $state<string>(initialMedicalNotes ?? '');
	let submitting = $state(false);
	let healthSectionOpen = $state(false);

	// Get selected type for display
	let selectedType = $derived(petTypes.find((t) => t.id === selectedTypeId));

	async function handleSubmit(e: Event) {
		e.preventDefault();
		if (!selectedTypeId) {
			return;
		}
		submitting = true;
		try {
			await onSubmit({
				name: name.trim(),
				birthDate,
				typeId: selectedTypeId,
				weight: weight ? parseFloat(weight) : null,
				lastVaccineDate: lastVaccineDate || null,
				allergies: allergies.trim() || null,
				dietaryRestrictions: dietaryRestrictions.trim() || null,
				medicalNotes: medicalNotes.trim() || null
			});
		} finally {
			submitting = false;
		}
	}
</script>

<form onsubmit={handleSubmit} class="space-y-6">
	<!-- Basic Information Section -->
	<div class="space-y-2">
		<Label for="name">Pet Name</Label>
		<Input
			id="name"
			bind:value={name}
			placeholder="Enter pet name"
			required
			disabled={submitting}
		/>
	</div>

	<div class="space-y-2">
		<Label for="birthDate">Birth Date</Label>
		<Input
			id="birthDate"
			type="date"
			bind:value={birthDate}
			required
			disabled={submitting}
		/>
	</div>

	<div class="space-y-2">
		<Label for="petType">Pet Type</Label>
		<Select.Root 
			type="single"
			value={selectedTypeId?.toString()}
			onValueChange={(value) => selectedTypeId = value ? Number(value) : undefined}
		>
			<Select.Trigger id="petType" class="w-full" disabled={submitting}>
				{selectedType?.name || 'Select a pet type'}
			</Select.Trigger>
			<Select.Content>
				{#each petTypes as petType (petType.id)}
					<Select.Item value={petType.id.toString()}>
						{petType.name}
					</Select.Item>
				{/each}
			</Select.Content>
		</Select.Root>
	</div>

	<!-- Health & Medical Information Section -->
	<Card.Root>
		<Card.Header>
			<button
				type="button"
				class="flex w-full items-center justify-between hover:opacity-70 transition-opacity"
				onclick={() => (healthSectionOpen = !healthSectionOpen)}
			>
				<Card.Title class="text-lg">Health & Medical Information</Card.Title>
				<ChevronDown 
					class="h-5 w-5 transition-transform flex-shrink-0 {healthSectionOpen ? 'rotate-180' : ''}"
				/>
			</button>
			<Card.Description>Optional health metrics and medical history</Card.Description>
		</Card.Header>

		{#if healthSectionOpen}
			<Card.Content class="space-y-4">
				<!-- Weight -->
				<div class="space-y-2">
					<Label for="weight">Weight (kg)</Label>
					<Input
						id="weight"
						type="number"
						step="0.1"
						min="0"
						bind:value={weight}
						placeholder="5.5"
						disabled={submitting}
					/>
				</div>

				<!-- Last Vaccine Date -->
				<div class="space-y-2">
					<Label for="lastVaccineDate">Last Vaccine Date</Label>
					<Input
						id="lastVaccineDate"
						type="date"
						bind:value={lastVaccineDate}
						disabled={submitting}
					/>
				</div>

				<!-- Allergies -->
				<div class="space-y-2">
					<Label for="allergies">Allergies</Label>
					<Textarea
						id="allergies"
						bind:value={allergies}
						placeholder="e.g., Penicillin, Dairy"
						disabled={submitting}
						class="min-h-[100px] sm:min-h-[80px]"
					/>
				</div>

				<!-- Dietary Restrictions -->
				<div class="space-y-2">
					<Label for="dietaryRestrictions">Dietary Restrictions</Label>
					<Textarea
						id="dietaryRestrictions"
						bind:value={dietaryRestrictions}
						placeholder="e.g., Grain-free, Low fat"
						disabled={submitting}
						class="min-h-[100px] sm:min-h-[80px]"
					/>
				</div>

				<!-- Medical Notes -->
				<div class="space-y-2">
					<Label for="medicalNotes">Medical Notes</Label>
					<Textarea
						id="medicalNotes"
						bind:value={medicalNotes}
						placeholder="Any relevant medical history..."
						disabled={submitting}
						class="min-h-[120px] sm:min-h-[96px]"
					/>
				</div>
			</Card.Content>
		{/if}
	</Card.Root>

	<!-- Action Buttons -->
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
			disabled={submitting || !selectedTypeId}
			class="w-full sm:w-auto"
		>
			{#if submitting}
				<Loader2 class="mr-2 h-4 w-4 animate-spin" />
			{/if}
			{submitLabel}
		</Button>
	</div>
</form>
