<script lang="ts">
	interface Props {
		open?: boolean;
		onOpenChange?: (open: boolean) => void;
	}

	let { open = $bindable(false), onOpenChange }: Props = $props();
</script>

<div class="collapsible">
	<slot { open } />
</div>

<style>
	.collapsible {
		width: 100%;
	}
</style>
