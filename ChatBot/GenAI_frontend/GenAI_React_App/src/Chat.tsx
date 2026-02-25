import { useEffect, useRef, useState } from "react";
import type { ChatMessage } from "./types";
import ReactMarkdown from "react-markdown";
import { Prism as SyntaxHighlighter } from "react-syntax-highlighter";
import { oneDark } from "react-syntax-highlighter/dist/esm/styles/prism";
import styles from "./StyleModules/Chat.module.css";
import remarkGfm from "remark-gfm";
import remarkBreaks from "remark-breaks";

interface ChatSession {
    id: string;
    title: string;
    messages: ChatMessage[];
}

const TypingIndicator = () => (
    <div className={styles.typingContainer}>
        <div className={styles.typingDot}></div>
        <div className={styles.typingDot}></div>
        <div className={styles.typingDot}></div>
    </div>
);

const EmptyState = () => (
    <div className={styles.emptyState}>
        <div className={styles.emptyIcon}>⚡</div>
        <div className={styles.emptyTitle}>GenAI Assistant</div>
        <div className={styles.emptySubtitle}>Ask anything to get started</div>
    </div>
);

export default function Chat() {
    const [sessions, setSessions] = useState<ChatSession[]>([
        {
            id: crypto.randomUUID(),
            title: "New Chat",
            messages: [],
        },
    ]);

    const [activeSessionId, setActiveSessionId] = useState(sessions[0].id);
    const [input, setInput] = useState("");
    const [loading, setLoading] = useState(false);
    const bottomRef = useRef<HTMLDivElement>(null);

    const activeSession = sessions.find((s) => s.id === activeSessionId)!;

    useEffect(() => {
        bottomRef.current?.scrollIntoView({ behavior: "smooth" });
    }, [activeSession.messages]);

    const updateSessionMessages = (messages: ChatMessage[]) => {
        setSessions((prev) =>
            prev.map((s) =>
                s.id === activeSessionId ? { ...s, messages } : s
            )
        );
    };

    const sendMessage = async () => {
        if (!input.trim() || loading) return;

        const userMessage: ChatMessage = { role: "user", content: input };
        const initialMessages = [...activeSession.messages, userMessage];

        updateSessionMessages([...initialMessages, { role: "assistant", content: "" }]);
        setInput("");
        setLoading(true);

        try {
            const response = await fetch(
                `http://localhost:8080/api/chat/stream?message=${encodeURIComponent(input)}`
            );

            const reader = response.body?.getReader();
            const decoder = new TextDecoder();
            let assistantText = "";
            let buffer = "";

            if (!reader) return;

            while (true) {
                const { done, value } = await reader.read();
                if (done) break;

                buffer += decoder.decode(value, { stream: true });

                const lines = buffer.split("\n");

                for (let i = 0; i < lines.length - 1; i++) {
                    const line = lines[i];
                    if (line.startsWith("data:")) {
                        const content = line.substring(5);
                        if (content === "") {
                            assistantText += "\n";
                        } else {
                            assistantText += content;
                        }
                    }
                }

                buffer = lines[lines.length - 1];

                updateSessionMessages([
                    ...initialMessages,
                    { role: "assistant", content: assistantText },
                ]);
            }
        } catch (error) {
            console.error("Stream error:", error);
        } finally {
            setLoading(false);
        }
    };

    const newChat = () => {
        const newSession: ChatSession = {
            id: crypto.randomUUID(),
            title: "New Chat",
            messages: [],
        };
        setSessions((prev) => [newSession, ...prev]);
        setActiveSessionId(newSession.id);
    };

    const clearChat = () => updateSessionMessages([]);

    return (
        <div className={styles.layout}>
            {/* ── Sidebar ── */}
            <div className={styles.sidebar}>
                <div className={styles.sidebarTop}>
                    <div className={styles.brandMark}>
                        <div className={styles.brandDot} />
                        <span className={styles.brandName}>GenAI</span>
                    </div>
                    <button className={styles.newChatBtn} onClick={newChat}>
                        <span>+</span> New Chat
                    </button>
                </div>

                <div className={styles.historySection}>
                    <div className={styles.historyLabel}>Recent</div>
                    {sessions.map((session) => (
                        <div
                            key={session.id}
                            className={`${styles.historyItem} ${
                                session.id === activeSessionId ? styles.activeHistory : ""
                            }`}
                            onClick={() => setActiveSessionId(session.id)}
                        >
                            {session.messages[0]?.content.slice(0, 28) || "Empty Chat"}
                        </div>
                    ))}
                </div>
            </div>

            {/* ── Chat Area ── */}
            <div className={styles.chatArea}>
                {/* Header */}
                <div className={styles.header}>
                    <span className={styles.headerTitle}>
                        {activeSession.messages[0]?.content.slice(0, 40) || "New Conversation"}
                    </span>
                    <div className={styles.headerActions}>
                        <button className={styles.clearBtn} onClick={clearChat}>
                            Clear
                        </button>
                    </div>
                </div>

                {/* Messages */}
                <div className={styles.chatBox}>
                    {activeSession.messages.length === 0 && <EmptyState />}

                    {activeSession.messages.map((msg, index) => (
                        <div
                            key={index}
                            className={
                                msg.role === "user"
                                    ? styles.userMessage
                                    : styles.botMessage
                            }
                        >
                            <ReactMarkdown
                                remarkPlugins={[remarkGfm, remarkBreaks]}
                                components={{
                                    code({ inline, className, children }) {
                                        const match = /language-(\w+)/.exec(className || "");

                                        if (!inline) {
                                            const rawCode = String(children).replace(/\n$/, "");
                                            const normalizedCode = rawCode
                                                .split("\n")
                                                .map((line) =>
                                                    line.replace(
                                                        /^\s+/,
                                                        (m) => " ".repeat(Math.floor(m.length / 4) * 4)
                                                    )
                                                )
                                                .join("\n");

                                            return (
                                                <div className={styles.codeBlockWrapper}>
                                                    <div className={styles.codeHeader}>
                                                        <span className={styles.codeLang}>
                                                            {match?.[1] || "code"}
                                                        </span>
                                                        <button
                                                            onClick={() =>
                                                                navigator.clipboard.writeText(normalizedCode)
                                                            }
                                                            className={styles.copyBtn}
                                                        >
                                                            Copy
                                                        </button>
                                                    </div>
                                                    <SyntaxHighlighter
                                                        style={oneDark}
                                                        language={match?.[1] || "text"}
                                                        PreTag="div"
                                                        wrapLongLines={true}
                                                        customStyle={{
                                                            margin: 0,
                                                            padding: "16px",
                                                            fontSize: "13px",
                                                            lineHeight: "1.6",
                                                            background: "#0a0e13",
                                                            fontFamily: "'JetBrains Mono', monospace",
                                                        }}
                                                    >
                                                        {normalizedCode}
                                                    </SyntaxHighlighter>
                                                </div>
                                            );
                                        }

                                        return (
                                            <code className={styles.inlineCode}>{children}</code>
                                        );
                                    },
                                }}
                            >
                                {msg.content}
                            </ReactMarkdown>

                            {msg.role === "assistant" &&
                                loading &&
                                index === activeSession.messages.length - 1 && (
                                    <TypingIndicator />
                                )}
                        </div>
                    ))}
                    <div ref={bottomRef} />
                </div>

                {/* Input */}
                <div className={styles.inputArea}>
                    <div className={styles.inputContainer}>
                        <input
                            className={styles.input}
                            value={input}
                            onChange={(e) => setInput(e.target.value)}
                            placeholder="Ask anything..."
                            onKeyDown={(e) => e.key === "Enter" && sendMessage()}
                        />
                        <button
                            className={styles.sendBtn}
                            onClick={sendMessage}
                            disabled={loading}
                        >
                            {loading ? "Thinking..." : "Send →"}
                        </button>
                    </div>
                </div>
            </div>
        </div>
    );
}