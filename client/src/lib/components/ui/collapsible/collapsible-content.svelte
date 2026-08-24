<script lang="ts">
	interface Props {
		class?: string;
	}

	let { class: className }: Props = $props();
</script>

<div class={`space-y-4 ${className || ''}`}>
	<slot />
</div>
