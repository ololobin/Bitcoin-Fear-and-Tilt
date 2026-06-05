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
        "Margin call has entered the chat"
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
        "Retirement plan is working"
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
        "Maximum sideways pain"
    )

    fun getMemePhrase(percent: Double, currentPrice: Double, language: String): String {
        val phrases = when {
            percent > 0.0 -> pumpMemes
            percent < 0.0 -> dumpMemes
            else -> flatMemes
        }

        val hash = abs(currentPrice.toString().hashCode())
        val index = hash % phrases.size
        return phrases[index]
    }
}
