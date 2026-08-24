<script lang="ts">
	interface Props {
		class?: string;
		disabled?: boolean;
		onclick?: (e: Event) => void;
	}

	let { class: className, disabled = false, onclick }: Props = $props();
</script>

<button 
	type="button"
	class={`inline-flex items-center justify-center rounded-md text-sm font-medium transition-colors focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-ring disabled:pointer-events-none disabled:opacity-50 ${className || ''}`}
	{disabled}
	{onclick}
>
	<slot />
</button>
