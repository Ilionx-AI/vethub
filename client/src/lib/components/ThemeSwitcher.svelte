<script lang="ts">
	import { Sun, Moon, Palette, Zap } from 'lucide-svelte';
	import { Button } from '$lib/components/ui/button';

	type Theme = 'light' | 'dark' | 'paw-patrol' | 'synesthesia';

	let currentTheme = $state<Theme>('light');
	let isOpen = $state(false);

	// Initialize theme from localStorage on mount
	$effect.pre(() => {
		if (typeof window !== 'undefined') {
			const saved = localStorage.getItem('theme') as Theme | null;
			const theme = saved || 'light';
			currentTheme = theme;
			applyTheme(theme);
		}
	});

	function applyTheme(theme: Theme) {
		const html = document.documentElement;
		
		// Remove all theme classes
		html.classList.remove('dark', 'paw-patrol', 'synesthesia');
		
		// Apply the selected theme class (light is default, no class needed)
		if (theme !== 'light') {
			html.classList.add(theme);
		}
		
		// Save to localStorage
		localStorage.setItem('theme', theme);
		currentTheme = theme;
	}

	function selectTheme(theme: Theme) {
		applyTheme(theme);
		isOpen = false;
	}

	function toggleDropdown() {
		isOpen = !isOpen;
	}

	// Close dropdown when clicking outside
	function handleClickOutside(event: MouseEvent) {
		const dropdown = document.getElementById('theme-dropdown');
		const button = document.getElementById('theme-button');
		
		if (dropdown && button && !dropdown.contains(event.target as Node) && !button.contains(event.target as Node)) {
			isOpen = false;
		}
	}
</script>

<svelte:document onmousedown={handleClickOutside} />

<div class="relative inline-block">
	<!-- Theme Button -->
	<Button
		id="theme-button"
		variant="ghost"
		size="icon"
		onclick={toggleDropdown}
		class="relative"
		aria-label="Toggle theme"
		aria-expanded={isOpen}
		aria-controls="theme-dropdown"
	>
		{#if currentTheme === 'light'}
			<Sun class="h-5 w-5" />
		{:else if currentTheme === 'dark'}
			<Moon class="h-5 w-5" />
		{:else if currentTheme === 'paw-patrol'}
			<Palette class="h-5 w-5" />
		{:else}
			<Zap class="h-5 w-5" />
		{/if}
	</Button>

	<!-- Theme Dropdown -->
	{#if isOpen}
		<div
			id="theme-dropdown"
			class="absolute right-0 z-50 mt-2 w-40 rounded-md border border-border bg-card shadow-lg animate-fade-in"
			role="menu"
		>
			<button
				onclick={() => selectTheme('light')}
				class="w-full px-4 py-2 text-left text-sm flex items-center gap-3 hover:bg-muted first:rounded-t-md {currentTheme ===
				'light'
					? 'bg-primary/10 text-primary font-medium'
					: 'text-foreground'}"
				role="menuitem"
			>
				<Sun class="h-4 w-4" />
				<span>Light</span>
				{#if currentTheme === 'light'}
					<span class="ml-auto text-xs">✓</span>
				{/if}
			</button>
			<button
				onclick={() => selectTheme('dark')}
				class="w-full px-4 py-2 text-left text-sm flex items-center gap-3 hover:bg-muted {currentTheme ===
				'dark'
					? 'bg-primary/10 text-primary font-medium'
					: 'text-foreground'}"
				role="menuitem"
			>
				<Moon class="h-4 w-4" />
				<span>Dark</span>
				{#if currentTheme === 'dark'}
					<span class="ml-auto text-xs">✓</span>
				{/if}
			</button>
			<button
				onclick={() => selectTheme('paw-patrol')}
				class="w-full px-4 py-2 text-left text-sm flex items-center gap-3 hover:bg-muted {currentTheme ===
				'paw-patrol'
					? 'bg-primary/10 text-primary font-medium'
					: 'text-foreground'}"
				role="menuitem"
			>
				<Palette class="h-4 w-4" />
				<span>Paw Patrol</span>
				{#if currentTheme === 'paw-patrol'}
					<span class="ml-auto text-xs">✓</span>
				{/if}
			</button>
			<button
				onclick={() => selectTheme('synesthesia')}
				class="w-full px-4 py-2 text-left text-sm flex items-center gap-3 hover:bg-muted last:rounded-b-md {currentTheme ===
				'synesthesia'
					? 'bg-primary/10 text-primary font-medium'
					: 'text-foreground'}"
				role="menuitem"
			>
				<Zap class="h-4 w-4" />
				<span>Synesthesia</span>
				{#if currentTheme === 'synesthesia'}
					<span class="ml-auto text-xs">✓</span>
				{/if}
			</button>
		</div>
	{/if}
</div>
