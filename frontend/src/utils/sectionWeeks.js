function parseSectionDate(value) {
  if (!value) return null
  const date = new Date(`${value}T00:00:00`)
  return Number.isNaN(date.getTime()) ? null : date
}

export function parseActiveWeeks(activeWeeks) {
  if (!activeWeeks) return []
  try {
    const parsed = JSON.parse(activeWeeks)
    if (!Array.isArray(parsed)) return []
    return parsed
      .map(Number)
      .filter(Number.isInteger)
      .sort((a, b) => a - b)
  } catch {
    return []
  }
}

function computeCalendarWeek(section, now = new Date()) {
  const start = parseSectionDate(section?.startDate)
  if (!start) return null
  const diffMs = now.getTime() - start.getTime()
  return Math.floor(diffMs / (7 * 24 * 60 * 60 * 1000)) + 1
}

export function getAllowedEvaluationWeek(section, now = new Date()) {
  const activeWeeks = parseActiveWeeks(section?.activeWeeks)
  if (!activeWeeks.length) return null

  const start = parseSectionDate(section?.startDate)
  const end = parseSectionDate(section?.endDate)
  if (!start || !end) {
    return activeWeeks.at(-1) ?? null
  }

  if (now < start) return null

  const calendarWeek = computeCalendarWeek(section, now)
  if (calendarWeek == null) return activeWeeks.at(-1) ?? null

  let candidateWeek
  const oneWeekAfterEnd = new Date(end)
  oneWeekAfterEnd.setDate(oneWeekAfterEnd.getDate() + 7)

  if (now <= oneWeekAfterEnd) {
    candidateWeek = calendarWeek - 1
  } else {
    // Course data in dev may be historical; fall back to the last active week.
    candidateWeek = activeWeeks.at(-1)
  }

  const eligibleWeeks = activeWeeks.filter(week => week <= candidateWeek)
  return eligibleWeeks.at(-1) ?? null
}

export function getEvaluationWeekOptions(section, now = new Date()) {
  const activeWeeks = parseActiveWeeks(section?.activeWeeks)
  if (!activeWeeks.length) return [1]
  const allowedWeek = getAllowedEvaluationWeek(section, now)
  return {
    activeWeeks,
    allowedWeek,
    defaultWeek: allowedWeek ?? activeWeeks.at(-1)
  }
}
