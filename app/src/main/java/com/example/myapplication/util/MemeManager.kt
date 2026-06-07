package com.example.myapplication.util

import kotlin.math.abs

object MemeManager {

    private val dumpMemes = listOf(
        "PANIC SELLING!",
        "DUMPPP!",
        "Going to 0! LOL",
        "Wendy's hiring",
        "McDonald's hiring",
        "Saylor sold 32 BTC!",
        "All money in AI",
        "Buttcoiners were right",
        "Peter Schiff: I told you so",
        "Inverse Cramer strikes again",
        "Reddit hotline pinned",
        "Exit liquidity gone",
        "Liquidated!",
        "RIP savings",
        "Buying the dip of the dip...",
        "Please pump, I have a family",
        "It is over. We are going to zero.",
        "Time to update the resume",
        "Stuck in exit liquidity support group",
        "Bulls are dead",
        "Just a healthy correction",
        "Who is selling at these prices?!",
        "Where is the bottom?!",
        "Selling blood for satoshis",
        "My portfolio is a comedy show",
        "Slightly used lambo for sale",
        "Trading is easy, they said",
        "Copium supply running low",
        "I should have bought gold instead",
        "Margin call has entered the chat",
        "GERMAN FUD",
        "MT.GOX MOVE",
        "SAYLOR LIQ",
        "MSTR MARGIN",
        "PUMP.FUN DUMP",
        "100x LIQ",
        "GUH!",
        "SOLANA DOWN",
        "WATER DIET",
        "CZ SELLS?",
        "GARY'S TARGET",
        "GENSLER",
        "NVDA ROTATE",
        "DUMPSTER RENT",
        "RAMEN DIET",
        "GURU RUN",
        "SPOUSE MAD",
        "BUS CARD",
        "TETHER FUD",
        "HALVING SCAM",
        "CRAMER BULL",
        "IBIT DUMP",
        "POWELL DELAY",
        "SAXONY DUMP",
        "GERMANY OUT",
        "SEC APPEAL",
        "SUMMER BLEED",
        "HOPE ZERO",
        "FEES > PORT",
        "BUY THE TOP",
        "CAT TRADES",
        "LOSS PORN",
        "WIFE'S BOYF",
        "BUY HIGH",
        "SELL LOW",
        "BAGHOLDER",
        "DUMP IT",
        "HE BOUGHT?",
        "GIGA REKT",
        "APE IN DUST",
        "CASINO CLOSE",
        "BTFD!"
    )

    private val pumpMemes = listOf(
        "HODL!",
        "To da moon!",
        "$100k incoming!",
        "WAGMI!",
        "HFSP",
        "ETF inflows go brrr",
        "Saylor is buying again",
        "Lambo when?",
        "In BTC we trust",
        "Goodbye Fiat!",
        "Up only!",
        "Study Bitcoin.",
        "1 BTC = 1 BTC",
        "Satoshi is smiling",
        "Easiest money of my life",
        "Rich by retirement",
        "Generational wealth loading",
        "HFSP fiat minds",
        "Green candles of happiness",
        "Shorts got absolutely wrecked!",
        "Bull run is officially back",
        "Don't sell your future!",
        "Bears in disbelief",
        "We are so back!",
        "Buying a whole island soon",
        "Stacking sats like a king",
        "My only regret is not buying more",
        "To Uranus and beyond!",
        "Retirement plan is working",
        "LASER EYES",
        "CRAMER BEAR",
        "SHORTS REKT",
        "BEARS REKT",
        "GOD CANDLE",
        "GIGA CANDLE",
        "FOMO ON",
        "GEN WEALTH",
        "QUIT MY JOB",
        "PUMP IT!",
        "BYE POVERTY",
        "EASY 100K",
        "TO THE MOON",
        "SAYLOR SMILE",
        "RICH SOON",
        "MOON WALK",
        "DIAMOND HAND",
        "LFG!",
        "HE SOLD?",
        "STONKS UP",
        "STONKS!",
        "MOON MISSION"
    )

    private val flatMemes = listOf(
        "Crab market",
        "Stablecoin BTC",
        "Calm before storm",
        "Boring!",
        "Poking with a stick...",
        "Sideways pain",
        "Wet noodle",
        "Wake me at $100k",
        "Checking charts at 3 AM",
        "Staring at the 1m chart",
        "Do something!",
        "Paint drying is more exciting",
        "Watching a stablecoin fluctuate",
        "Consolidation purgatory",
        "Did Satoshi pause the blockchain?",
        "Crab walk in full effect",
        "Volume has left the chat",
        "Flatline like my heartbeat",
        "Even the bots are asleep",
        "Nothing to see here, go outside",
        "Volatility is dead",
        "Are we there yet?",
        "Patiently waiting for the breakout",
        "Maximum sideways pain",
        "TOUCH GRASS",
        "STABLECOIN",
        "FLATLINE",
        "ZERO VOL",
        "BOTS ONLY",
        "CHART FROZE",
        "STILL 69K",
        "PAUSE BLOCK",
        "CRAB LIFE",
        "NAP TIME",
        "TICK TOCK",
        "CRAB DANCE",
        "NO SIGNALS",
        "STUCK HERE",
        "SIDEWAYS",
        "CHART SLEEP",
        "NO VOLATILE",
        "DRY PAINT"
    )

    fun getMemePhrase(percent: Double, currentPrice: Double, language: String): String {
        return getMemePhrase(percent, 2.0, currentPrice, language)
    }

    fun getMemePhrase(percent: Double, speedThreshold: Double, currentPrice: Double, language: String): String {
        val phrases = when {
            abs(percent) <= speedThreshold -> flatMemes
            percent > 0.0 -> pumpMemes
            else -> dumpMemes
        }

        val hash = abs(currentPrice.toString().hashCode())
        val index = hash % phrases.size
        return phrases[index]
    }

    fun wrapText(text: String, maxLineLength: Int): String {
        if (text.length <= maxLineLength) return text
        val words = text.split(" ")
        val sb = StringBuilder()
        var currentLineLength = 0
        for (i in words.indices) {
            val word = words[i]
            val addedLength = word.length + (if (currentLineLength > 0) 1 else 0)
            if (currentLineLength + addedLength <= maxLineLength) {
                if (currentLineLength > 0) {
                    sb.append(" ")
                }
                sb.append(word)
                currentLineLength += addedLength
            } else {
                if (currentLineLength > 0) {
                    sb.append("\n")
                }
                sb.append(word)
                currentLineLength = word.length
            }
        }
        return sb.toString()
    }
}

