/**
 * Health metrics utility functions for pet health data display and calculations
 */

/**
 * Calculate the number of days since a given date
 * @param dateStr ISO date string or Date object
 * @returns Number of days since the date, or null if date is invalid
 */
export function daysSinceDate(dateStr: string | Date | undefined | null): number | null {
	if (!dateStr) return null;
	try {
		const date = typeof dateStr === 'string' ? new Date(dateStr) : dateStr;
		if (isNaN(date.getTime())) return null;
		const now = new Date();
		const diff = now.getTime() - date.getTime();
		return Math.floor(diff / (1000 * 60 * 60 * 24));
	} catch {
		return null;
	}
}

/**
 * Calculate days since vaccination
 * @param lastVaccineDate ISO date string
 * @returns Number of days since vaccine, or null if not vaccinated
 */
export function daysSinceVaccine(lastVaccineDate: string | Date | undefined | null): number | null {
	return daysSinceDate(lastVaccineDate);
}

/**
 * Get vaccine status color based on days since vaccination
 * @param daysSince Number of days since vaccination
 * @returns Color class name: 'text-green-600', 'text-yellow-600', or 'text-red-600'
 */
export function getVaccineStatusColor(daysSince: number | null): string {
	if (daysSince === null || daysSince === undefined) return 'text-red-600';
	if (daysSince < 90) return 'text-green-600';
	if (daysSince < 180) return 'text-yellow-600';
	return 'text-red-600';
}

/**
 * Get vaccine status badge variant
 * @param daysSince Number of days since vaccination
 * @returns Badge variant: 'default', 'secondary', or 'destructive'
 */
export function getVaccineStatusBadgeVariant(daysSince: number | null): 'default' | 'secondary' | 'destructive' {
	if (daysSince === null || daysSince === undefined) return 'destructive';
	if (daysSince < 90) return 'default';
	if (daysSince < 180) return 'secondary';
	return 'destructive';
}

/**
 * Get vaccine status label
 * @param daysSince Number of days since vaccination
 * @returns Human-readable status: "Current", "Due Soon", or "Overdue"
 */
export function getVaccineStatusLabel(daysSince: number | null): string {
	if (daysSince === null || daysSince === undefined) return 'Not Recorded';
	if (daysSince < 90) return 'Current';
	if (daysSince < 180) return 'Due Soon';
	return 'Overdue';
}

/**
 * Format vaccine date with human-readable text
 * @param dateStr ISO date string
 * @returns Formatted string like "Vaccinated 15 days ago" or "Not recorded"
 */
export function formatVaccineDate(dateStr: string | Date | undefined | null): string {
	const days = daysSinceVaccine(dateStr);
	if (days === null) return 'Not recorded';
	if (days === 0) return 'Vaccinated today';
	if (days === 1) return 'Vaccinated yesterday';
	return `Vaccinated ${days} days ago`;
}

/**
 * Calculate pet age in years
 * @param birthDate ISO date string
 * @returns Age in years as a number
 */
export function calculateAge(birthDate: string | Date | undefined): number | null {
	if (!birthDate) return null;
	try {
		const birth = typeof birthDate === 'string' ? new Date(birthDate) : birthDate;
		if (isNaN(birth.getTime())) return null;
		const now = new Date();
		const years = Math.floor((now.getTime() - birth.getTime()) / (365.25 * 24 * 60 * 60 * 1000));
		return Math.max(0, years);
	} catch {
		return null;
	}
}

/**
 * Format pet age as human-readable string
 * @param birthDate ISO date string
 * @returns Age string like "5 years old" or "8 months old"
 */
export function formatAge(birthDate: string | Date | undefined): string {
	if (!birthDate) return 'Unknown age';
	try {
		const birth = typeof birthDate === 'string' ? new Date(birthDate) : birthDate;
		if (isNaN(birth.getTime())) return 'Unknown age';
		const now = new Date();
		const years = Math.floor((now.getTime() - birth.getTime()) / (365.25 * 24 * 60 * 60 * 1000));
		if (years === 0) {
			const months = Math.floor((now.getTime() - birth.getTime()) / (30.44 * 24 * 60 * 60 * 1000));
			return months <= 1 ? '< 1 month old' : `${months} months old`;
		}
		return years === 1 ? '1 year old' : `${years} years old`;
	} catch {
		return 'Unknown age';
	}
}
