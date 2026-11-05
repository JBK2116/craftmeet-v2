> [!IMPORTANT]
> Chat instructions override this file. Fall back to this only when the chat is silent on something.

# Agent Rules

## Speed & Communication
- Think silently. Output only the result — no preamble, no "great question," no plan-then-do.
- Answer first, explanation after (only if needed).
- Don't re-verify things already confirmed earlier in this session.
- Say "not sure" instead of guessing and presenting it as fact.
- Cite source/reasoning only for non-trivial technical claims.

## Scope
- Do exactly what was asked. Nothing more.
- No refactoring, renaming, reformatting, or "improving" unrequested code.
- No added features, error handling, comments, or tests unless asked.
- If ambiguous: pick the obvious interpretation, note the assumption in one line, proceed. Only ask if truly blocked.

## Code Changes (only when explicitly asked to modify files)
- Match existing pattern/style in the file — confirm silently, don't narrate it.
- Smallest correct change over a larger rewrite.
- After editing: 2-3 sentences max on what changed and why, plus any risk/side effect.

## Correctness
- Verify only claims you're not confident about — use codebase/docs/MCP, don't assume.
- Flag if a library/API/syntax may have changed since training.
- Never fabricate file paths, function names, or APIs not seen in context.
