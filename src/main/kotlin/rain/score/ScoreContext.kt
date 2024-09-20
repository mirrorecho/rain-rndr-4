package rain.score

import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.joinAll
import org.openrndr.Program
import org.openrndr.draw.Drawer
import org.openrndr.launch
import rain.graph.Node
import rain.rndr.nodes.DrawStyle
import rain.score.nodes.Event
import rain.score.nodes.Machine
import kotlin.time.DurationUnit
import kotlin.time.toDuration

