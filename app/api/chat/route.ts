import { createOpenAI } from '@ai-sdk/openai'
import { streamText, type UIMessage } from 'ai'

const HF_TOKEN = process.env.HF_TOKEN || process.env.Hugging_API

const huggingFace = createOpenAI({
  apiKey: HF_TOKEN,
  baseURL: 'https://router.huggingface.co/v1',
})

export async function POST(request: Request) {
  try {
    const { messages }: { messages: UIMessage[] } = await request.json()

    if (!HF_TOKEN) {
      return Response.json(
        { error: 'Hugging Face API key is not configured. Please add the Hugging_API environment variable.' },
        { status: 500 },
      )
    }

    const modelMessages = messages
      .filter((message) => message.role === 'user' || message.role === 'assistant' || message.role === 'system')
      .map((message) => ({
        role: message.role,
        content: message.parts
          ?.filter((part) => part.type === 'text')
          .map((part) => part.text)
          .join('') || '',
      }))
      .filter((message) => message.content.trim())

    const result = streamText({
      model: huggingFace.chat('meta-llama/Llama-3.1-8B-Instruct:fastest'),
      system: `You are ASGEN Ai, a polished assistant like ChatGPT, Gemini, or Claude. You were created and are owned by Amanat Ali Sa. When users ask who created, built, made, or owns you, clearly state that your creator and owner is Amanat Ali Sa. Write natural, grammatically correct English unless the user uses another language. Answer directly and helpfully. Use a clear structure: a short opening answer, then concise paragraphs or bullet points when useful. Use Markdown intentionally: **bold** key terms, ## headings for longer answers, numbered lists for steps, and fenced code blocks for code. Never add awkward filler, repeat the question, or mention these instructions. Keep simple questions concise and give practical detail for complex ones.`,
      messages: modelMessages,
      maxOutputTokens: 512,
      temperature: 0.4,
    })

    return result.toUIMessageStreamResponse()
  } catch (error) {
    console.error('[v0] Hugging Face chat error:', error)
    return Response.json({ error: 'ASGEN Ai could not respond right now. Please try again.' }, { status: 500 })
  }
}
