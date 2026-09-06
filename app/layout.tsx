import { Analytics } from '@vercel/analytics/next'
import type { Metadata, Viewport } from 'next'
import './globals.css'

export const metadata: Metadata = {
  title: 'ASGEN Ai — Think clearly. Create freely.',
  description: 'A smooth, intelligent AI workspace for ideas, answers, and getting things done.',
  generator: 'ASGEN Ai',
}

export const viewport: Viewport = {
  colorScheme: 'dark light',
  themeColor: '#111318',
}

export default function RootLayout({ children }: Readonly<{ children: React.ReactNode }>) {
  return <html lang="en" className="bg-background"><body className="antialiased">{children}{process.env.NODE_ENV === 'production' && <Analytics />}</body></html>
}
