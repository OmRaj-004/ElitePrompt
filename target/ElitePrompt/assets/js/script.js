/**
 * Elite Prompt - Custom JavaScript
 * TYBCA Mini Project - Simple, readable code
 */

// Prompt templates for each category (mirrors PromptGenerator.java)
const promptTemplates = {
    "Education": "You are an expert tutor and educator.",
    "Business": "You are a senior business consultant with 15 years of experience.",
    "Content Creation": "You are an expert social media content creator.",
    "Programming": "You are a senior software developer and coding mentor.",
    "Marketing": "You are an expert digital marketer and copywriter.",
    "Resume & Career": "You are a professional career coach and resume expert.",
    "General": "You are a helpful AI assistant."
};

// Generate structured prompt from form values
function buildPrompt(category, requirement, tone, outputFormat) {
    const role = promptTemplates[category] || promptTemplates["General"];
    let prompt = role + " ";

    // Add the user requirement
    prompt += requirement.trim();
    if (!prompt.endsWith(".")) {
        prompt += ".";
    }

    // Add tone
    prompt += " Use a " + tone.toLowerCase() + " tone.";

    // Add output format
    prompt += " Provide the output in " + outputFormat.toLowerCase() + " format.";

    // Add closing instruction
    prompt += " Keep it clear, concise and actionable.";

    return prompt;
}

// Quality score mirror of Java PromptGenerator.score() (same checklist)
function scoreRequirement(requirement) {
    if (!requirement || !requirement.trim()) return 0;
    const req = requirement.trim().toLowerCase();
    const verbs = ["create", "write", "explain", "generate", "design", "analyze",
        "summarize", "summarise", "list", "compare", "draft", "plan", "suggest"];
    let points = 40;
    if (req.length >= 20) points += 15;
    if (req.split(/\s+/).length >= 8) points += 15;
    if (verbs.some(v => req.includes(v))) points += 10;
    if (req.includes("for ") || req.includes("with ") || req.includes("audience")
        || req.includes("students") || req.includes("customers")) points += 10;
    if (req.includes("list") || req.includes("table") || req.includes("steps")
        || req.includes("example") || req.includes("points")) points += 10;
    return Math.min(points, 100);
}

// Show live score for the current requirement text
function showLiveScore() {
    const box = document.getElementById("liveScore");
    const req = document.getElementById("requirement");
    if (!box || !req) return;
    const s = scoreRequirement(req.value);
    box.innerHTML = '<span class="badge ' + (s >= 80 ? 'bg-success' : (s >= 60 ? 'bg-warning text-dark' : 'bg-danger')) + '">Live Score: ' + s + '/100</span>';
}

// Handle Generate button on prompt_generator.jsp
function handleGenerate() {
    const requirement = document.getElementById("requirement");
    const category = document.getElementById("category");
    const tone = document.getElementById("tone");
    const outputFormat = document.getElementById("output_format");
    const resultBox = document.getElementById("generatedPrompt");
    const resultSection = document.getElementById("resultSection");

    if (!requirement || !resultBox) return;

    if (requirement.value.trim() === "") {
        alert("Please enter your requirement first.");
        requirement.focus();
        return;
    }

    const text = buildPrompt(
        category.value,
        requirement.value,
        tone.value,
        outputFormat.value
    );

    resultBox.textContent = text;
    document.getElementById("generated_prompt_input").value = text;

    if (resultSection) {
        resultSection.style.display = "block";
        resultSection.scrollIntoView({ behavior: "smooth" });
    }
}

// Copy generated prompt to clipboard
function copyPrompt() {
    const box = document.getElementById("generatedPrompt");
    if (!box || !box.textContent.trim()) {
        alert("Nothing to copy. Generate a prompt first.");
        return;
    }
    navigator.clipboard.writeText(box.textContent.trim()).then(() => {
        alert("Prompt copied to clipboard!");
    }).catch(() => {
        // Fallback for older browsers
        const temp = document.createElement("textarea");
        temp.value = box.textContent.trim();
        document.body.appendChild(temp);
        temp.select();
        document.execCommand("copy");
        document.body.removeChild(temp);
        alert("Prompt copied to clipboard!");
    });
}

// Copy any text from a given element ID
function copyTextById(elementId) {
    const el = document.getElementById(elementId);
    if (!el) return;
    navigator.clipboard.writeText(el.innerText).then(() => {
        alert("Copied!");
    });
}

// Live search filter for my_prompts.jsp cards
function filterPrompts() {
    const search = document.getElementById("searchInput");
    const category = document.getElementById("filterCategory");
    if (!search) return;

    const keyword = search.value.toLowerCase();
    const selectedCat = category ? category.value : "";

    const items = document.querySelectorAll(".prompt-item");
    items.forEach((item) => {
        const text = item.innerText.toLowerCase();
        const itemCat = item.getAttribute("data-category") || "";
        const matchSearch = text.includes(keyword);
        const matchCat = selectedCat === "" || itemCat === selectedCat;
        item.style.display = (matchSearch && matchCat) ? "" : "none";
    });
}

// Confirm before delete
function confirmDelete() {
    return confirm("Are you sure you want to delete this prompt?");
}

// Back to top button + navbar shadow
document.addEventListener("DOMContentLoaded", () => {
    const backBtn = document.getElementById("backToTop");

    window.addEventListener("scroll", () => {
        if (window.scrollY > 300) {
            backBtn.style.display = "block";
        } else {
            backBtn.style.display = "none";
        }
    });

    if (backBtn) {
        backBtn.addEventListener("click", () => {
            window.scrollTo({ top: 0, behavior: "smooth" });
        });
    }

    // Attach live filter if present
    const search = document.getElementById("searchInput");
    if (search) {
        search.addEventListener("keyup", filterPrompts);
    }
    const filterCat = document.getElementById("filterCategory");
    if (filterCat) {
        filterCat.addEventListener("change", filterPrompts);
    }

    // Attach generate button if present
    const genBtn = document.getElementById("generateBtn");
    if (genBtn) {
        genBtn.addEventListener("click", handleGenerate);
    }
});

// Lightweight markdown renderer for AI answers.
// Escapes HTML first, then converts common markdown to styled HTML.
// Supports: headings, bold, italic, inline code, code blocks,
// unordered/ordered lists, blockquotes, and horizontal rules.
function escapeHtml(text) {
    return text
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;");
}

function renderMarkdown(text) {
    if (!text) return "";
    let html = escapeHtml(text);

    // Code blocks first (``` or ~~~), so markdown inside them is untouched
    html = html.replace(/```[\w-]*\n?([\s\S]*?)```/g, function (_, code) {
        return '<pre class="md-pre"><code>' + code.trim() + '</code></pre>';
    });

    // Headings (# .. ######)
    html = html.replace(/^###### (.*)$/gm, "<h6 class='md-h'>$1</h6>");
    html = html.replace(/^##### (.*)$/gm, "<h5 class='md-h'>$1</h5>");
    html = html.replace(/^#### (.*)$/gm, "<h4 class='md-h'>$1</h4>");
    html = html.replace(/^### (.*)$/gm, "<h3 class='md-h'>$1</h3>");
    html = html.replace(/^## (.*)$/gm, "<h2 class='md-h'>$1</h2>");
    html = html.replace(/^# (.*)$/gm, "<h1 class='md-h'>$1</h1>");

    // Bold and italic (before inline code, so ** doesn't break code)
    html = html.replace(/\*\*\*(.+?)\*\*\*/g, "<strong><em>$1</em></strong>");
    html = html.replace(/\*\*(.+?)\*\*/g, "<strong>$1</strong>");
    html = html.replace(/__(.+?)__/g, "<strong>$1</strong>");
    html = html.replace(/\*(.+?)\*/g, "<em>$1</em>");
    html = html.replace(/_(.+?)_/g, "<em>$1</em>");

    // Inline code (`...`)
    html = html.replace(/`([^`]+)`/g, "<code class='md-code'>$1</code>");

    // Blockquote
    html = html.replace(/^&gt; (.*)$/gm, "<blockquote class='md-quote'>$1</blockquote>");

    // Horizontal rule
    html = html.replace(/^(---|\*\*\*|___)\s*$/gm, "<hr class='md-hr'>");

    // Unordered list: consecutive - or * or + lines
    html = html.replace(/^((?:[-*+] .*(?:\n|$))+)/gm, function (block) {
        const items = block.trim().split("\n").map(l => {
            const m = l.match(/^[-*+] (.+)$/);
            return m ? "<li>" + m[1] + "</li>" : "";
        }).join("");
        return "<ul class='md-ul'>" + items + "</ul>";
    });

    // Ordered list: consecutive "1. " style lines
    html = html.replace(/^((?:\d+\. .*(?:\n|$))+)/gm, function (block) {
        const items = block.trim().split("\n").map(l => {
            const m = l.match(/^\d+\. (.+)$/);
            return m ? "<li>" + m[1] + "</li>" : "";
        }).join("");
        return "<ol class='md-ol'>" + items + "</ol>";
    });

    // Paragraphs: wrap double-newline separated blocks that aren't already tags
    html = html.split(/\n{2,}/).map(block => {
        const trimmed = block.trim();
        if (!trimmed) return "";
        if (/^<(h\d|ul|ol|pre|blockquote|hr)/.test(trimmed)) return trimmed;
        return "<p class='md-p'>" + trimmed.replace(/\n/g, "<br>") + "</p>";
    }).join("\n");

    return html;
}

// Render any element that carries a data-markdown attribute
function renderMarkdownElements() {
    document.querySelectorAll("[data-markdown]").forEach(el => {
        const raw = el.textContent || el.innerHTML;
        el.innerHTML = renderMarkdown(raw);
        el.classList.add("md");
    });
}

document.addEventListener("DOMContentLoaded", renderMarkdownElements);
