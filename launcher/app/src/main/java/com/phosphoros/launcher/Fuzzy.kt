package com.phosphoros.launcher

object Fuzzy {
    /**
     * Higher score is better. Returns null if no plausible match.
     */
    fun bestMatch(query: String, candidates: List<String>): String? {
        val q = query.trim().lowercase()
        if (q.isEmpty()) return null

        var best: Pair<String, Int>? = null
        for (c in candidates) {
            val s = score(q, c.lowercase())
            if (s <= 0) continue
            if (best == null || s > best.second) best = c to s
        }
        return best?.first
    }

    private fun score(q: String, c: String): Int {
        if (c == q) return 1000
        if (c.startsWith(q)) return 700 - (c.length - q.length)
        if (c.contains(q)) return 400 - (c.length - q.length)

        // Subsequence bonus (e.g. "chr" -> "chrome")
        val subseq = subsequenceScore(q, c)
        if (subseq > 0) return 200 + subseq

        return 0
    }

    private fun subsequenceScore(q: String, c: String): Int {
        var qi = 0
        var score = 0
        for (ch in c) {
            if (qi < q.length && ch == q[qi]) {
                qi++
                score += 10
            }
        }
        return if (qi == q.length) score else 0
    }
}

