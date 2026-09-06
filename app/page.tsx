'use client'

import { useChat } from '@ai-sdk/react'
import { useEffect, useRef, useState } from 'react'
import { ArrowUp, Check, Copy, LogIn, LogOut, Menu, Moon, Plus, Sparkles, Sun, Trash2, User, X } from 'lucide-react'
import { getRedirectResult, onAuthStateChanged, GoogleAuthProvider, signInWithPopup, signInWithRedirect, signOut as firebaseSignOut, type User as FirebaseUser } from 'firebase/auth'
import { firebaseAuth } from '@/lib/firebase/client'

export default function Page() {
  const [dark, setDark] = useState(true)
  const [sidebarOpen, setSidebarOpen] = useState(false)
  const [input, setInput] = useState('')
  const [copied, setCopied] = useState<string | null>(null)
  const [files, setFiles] = useState<File[]>([])
  const [conversations, setConversations] = useState<{ id: string; title: string; messages: typeof messages }[]>([])
  const [activeConversationId, setActiveConversationId] = useState<string | null>(null)
  const [firebaseUser, setFirebaseUser] = useState<FirebaseUser | null>(null)
  const [authError, setAuthError] = useState<string | null>(null)
  const fileInputRef = useRef<HTMLInputElement>(null)
  const skipConversationSyncRef = useRef(false)
  const { messages, setMessages, sendMessage, status, stop } = useChat()

  useEffect(() => {
    if (!firebaseAuth) return
    const unsubscribe = onAuthStateChanged(firebaseAuth, setFirebaseUser)
    getRedirectResult(firebaseAuth).catch((error: { code?: string }) => {
      if (error.code && error.code !== 'auth/popup-closed-by-user') {
        setAuthError(getGoogleAuthError(error.code))
      }
    })
    return unsubscribe
  }, [])

  function getGoogleAuthError(code?: string) {
    if (code === 'auth/operation-not-allowed') return 'Google login is disabled in Firebase. Enable Google in Firebase Authentication → Sign-in method.'
    if (code === 'auth/unauthorized-domain') return `Firebase blocked this website (${typeof window !== 'undefined' ? window.location.hostname : 'current preview'}). Add this exact domain in Firebase Console → Authentication → Settings → Authorized domains, then reload ASGEN AI.`
    if (code === 'auth/invalid-api-key') return 'Firebase API key is invalid. Check the web app Firebase configuration.'
    if (code === 'auth/popup-blocked') return 'Popup was blocked, so opening Google login in this window.'
    if (code === 'auth/popup-closed-by-user') return 'Google login was cancelled.'
    return 'Google login could not be completed. Check Firebase Google provider and try again.'
  }

  async function signInWithGoogle() {
    setAuthError(null)
    if (!firebaseAuth) {
      setAuthError('Firebase is not configured for this preview.')
      return
    }
    try {
      await signInWithPopup(firebaseAuth, new GoogleAuthProvider())
    } catch (error: unknown) {
      const code = (error as { code?: string }).code
      if (code === 'auth/popup-blocked') {
        setAuthError('Opening Google login...')
        await signInWithRedirect(firebaseAuth, new GoogleAuthProvider())
        return
      }
      setAuthError(getGoogleAuthError(code))
    }
  }

  async function signOut() {
    if (firebaseAuth) await firebaseSignOut(firebaseAuth)
  }

  function createConversation() {
    const id = crypto.randomUUID()
    skipConversationSyncRef.current = true
    setConversations((current) => [{ id, title: 'New conversation', messages: [] }, ...current])
    setMessages([])
    setActiveConversationId(id)
    setInput('')
    setFiles([])
    setSidebarOpen(false)
  }

  function loadConversation(conversation: { id: string; title: string; messages: typeof messages }) {
    skipConversationSyncRef.current = true
    setMessages([...conversation.messages])
    setActiveConversationId(conversation.id)
    setInput('')
    setFiles([])
    setSidebarOpen(false)
  }

  useEffect(() => {
    if (skipConversationSyncRef.current) {
      skipConversationSyncRef.current = false
      return
    }
    if (!activeConversationId || messages.length === 0 || status === 'submitted' || status === 'streaming') return
    setConversations((current) => current.map((conversation) => conversation.id === activeConversationId ? {
      ...conversation,
      title: conversation.title === 'New conversation' ? (messages.find((message) => message.role === 'user')?.parts?.find((part) => part.type === 'text')?.text.slice(0, 32) || conversation.title) : conversation.title,
      messages: [...messages],
    } : conversation))
  }, [messages, activeConversationId, status])

  async function submit(text = input) {
    const value = text.trim()
    if (!value || status === 'submitted' || status === 'streaming') return
    if (!activeConversationId) {
      const id = crypto.randomUUID()
      setActiveConversationId(id)
      setConversations((current) => [{ id, title: value.slice(0, 32), messages: [] }, ...current])
    }
    setInput('')
    const attachmentNote = files.length ? `\nAttached files: ${files.map((file) => file.name).join(', ')}` : ''
    sendMessage({ text: value + attachmentNote })
    setFiles([])
  }

  function copyMessage(id: string, text: string) {
    navigator.clipboard.writeText(text)
    setCopied(id)
    window.setTimeout(() => setCopied(null), 1400)
  }

  function renderInlineMarkdown(value: string) {
    return value.split(/(\*\*[^*]+\*\*|`[^`]+`)/g).map((part, index) => {
      if (part.startsWith('**') && part.endsWith('**')) return <strong className="response-bold" key={`bold-${index}`}>{part.slice(2, -2)}</strong>
      if (part.startsWith('`') && part.endsWith('`')) return <code className="inline-code" key={`inline-code-${index}`}>{part.slice(1, -1)}</code>
      return part
    })
  }

  function renderMessageText(text: string, role: string) {
    if (role === 'user') return text
    return text.split(/```/).map((section, index) => {
      if (index % 2 === 1) return <pre className="code-block" key={`code-${index}`}><code>{section.trim()}</code></pre>
      return section.split(/\n\s*\n/).filter(Boolean).map((paragraph, paragraphIndex) => {
        const lines = paragraph.split('\n')
        return <div className="response-block" key={`paragraph-${index}-${paragraphIndex}`}>{lines.map((line, lineIndex) => {
          const headingMatch = line.match(/^#{1,3}\s+(.+)$/)
          const listMatch = line.match(/^\s*(?:[-*]|\d+\.)\s+(.*)$/)
          if (headingMatch) return <h3 className="response-heading" key={`heading-${lineIndex}`}>{renderInlineMarkdown(headingMatch[1])}</h3>
          return <span className={listMatch ? 'response-list-item' : 'response-line'} key={`line-${lineIndex}`}>{listMatch ? <>• {renderInlineMarkdown(listMatch[1])}</> : renderInlineMarkdown(line)}{lineIndex < lines.length - 1 && !listMatch ? <br /> : null}</span>
        })}</div>
      })
    })
  }

  return (
    <div className={dark ? 'app-shell dark' : 'app-shell'}>
      <aside className={sidebarOpen ? 'sidebar sidebar-open' : 'sidebar'}>
        <div className="brand-row"><div className="brand-mark"><Sparkles size={16} /></div><span>ASGEN <b>Ai</b></span><button className="icon-button mobile-close" onClick={() => setSidebarOpen(false)} aria-label="Close menu"><X size={18} /></button></div>
        <button className="new-chat" onClick={createConversation}><Plus size={18} /> New conversation <span>⌘ K</span></button>
        <div className="history-label">Recent</div>
        <nav className="history" aria-label="Conversation history">
          {conversations.length === 0 ? <p className="history-empty">No conversations yet</p> : conversations.map((conversation) => <button key={conversation.id} className={conversation.id === activeConversationId ? 'history-item active' : 'history-item'} onClick={() => loadConversation(conversation)}>{conversation.title}</button>)}
        </nav>
        <div className="sidebar-bottom"><button className="sidebar-link" onClick={() => { setConversations([]); setActiveConversationId(null); setMessages([]) }}><Trash2 size={16} /> Clear conversations</button>{firebaseUser ? <div className="profile"><div className="avatar">{(firebaseUser.displayName ?? firebaseUser.email ?? 'AI').slice(0, 2).toUpperCase()}</div><div><strong>{firebaseUser.displayName ?? 'ASGEN user'}</strong><small>{firebaseUser.email}</small></div><button className="icon-button" onClick={signOut} aria-label="Sign out"><LogOut size={16} /></button></div> : <button className="sidebar-link" onClick={signInWithGoogle}><LogIn size={16} /> Continue with Google</button>}{authError && <p className="auth-error" role="alert">{authError}</p>}</div>
      </aside>
      {sidebarOpen && <button className="scrim" onClick={() => setSidebarOpen(false)} aria-label="Close sidebar" />}
      <main className="chat-area">
        <header className="topbar"><button className="icon-button menu-button" onClick={() => setSidebarOpen(true)} aria-label="Open menu"><Menu size={20} /></button><div className="model-pill"><span className="status-dot" /> ASGEN AI <span className="chevron">⌄</span></div><div className="top-actions"><button className="icon-button" onClick={() => setDark(!dark)} aria-label="Toggle theme">{dark ? <Sun size={18} /> : <Moon size={18} />}</button><button className="share-button">Share</button></div></header>
        <section className="conversation" aria-live="polite">
          {messages.length === 0 ? <div className="welcome"><div className="welcome-icon"><Sparkles size={25} /></div><h1>How can I help you today?</h1><p>Ask anything, explore ideas, or get things done with ASGEN Ai.</p></div> : <div className="messages">{messages.map((message) => { const text = message.parts?.filter((part) => part.type === 'text').map((part) => part.text).join('') || ''; return <div className={message.role === 'user' ? 'message user-message' : 'message assistant-message'} key={message.id}><div className="message-avatar">{message.role === 'user' ? <User size={15} /> : <Sparkles size={15} />}</div><div className="message-body"><div className="message-name">{message.role === 'user' ? 'You' : 'ASGEN Ai'}</div><div className="message-text">{text ? renderMessageText(text, message.role) : (status === 'submitted' || status === 'streaming' ? <span className="thinking-state">Thinking<span>...</span></span> : '')}</div>{message.role === 'assistant' && text && <div className="message-tools"><button onClick={() => copyMessage(message.id, text)}>{copied === message.id ? <Check size={14} /> : <Copy size={14} />} {copied === message.id ? 'Copied' : 'Copy'}</button></div>}</div></div> })}</div>}
        </section>
        <footer className="composer-wrap"><div className="composer">{files.length > 0 && <div className="file-list" aria-live="polite">{files.map((file) => <span className="file-chip" key={file.name}>{file.name}<button type="button" onClick={() => setFiles((current) => current.filter((item) => item !== file))} aria-label={`Remove ${file.name}`}>×</button></span>)}</div>}<textarea value={input} onChange={(e) => setInput(e.target.value)} onKeyDown={(e) => { if (e.key === 'Enter' && !e.shiftKey && !e.nativeEvent.isComposing && e.keyCode !== 229) { e.preventDefault(); submit() } }} placeholder="Message ASGEN Ai..." rows={1} aria-label="Message ASGEN Ai" /><input ref={fileInputRef} type="file" multiple accept="image/*,.pdf,.txt,.doc,.docx" className="sr-only" onChange={(e) => setFiles(Array.from(e.target.files ?? []))} /><div className="composer-actions"><button type="button" className="attach-button" onClick={() => fileInputRef.current?.click()} aria-label="Add image or file">+</button><span className="hint">Shift + Enter for new line</span>{status === 'submitted' || status === 'streaming' ? <button className="stop-button" onClick={() => stop()} aria-label="Stop generating"><span /></button> : <button className="send-button" onClick={() => submit()} disabled={!input.trim()} aria-label="Send message"><ArrowUp size={17} /></button>}</div></div><p className="disclaimer">ASGEN Ai can make mistakes. Check important info.</p></footer>
      </main>
    </div>
  )
}
