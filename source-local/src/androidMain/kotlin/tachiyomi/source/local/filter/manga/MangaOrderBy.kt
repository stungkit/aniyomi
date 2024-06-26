package tachiyomi.source.local.filter.manga

import android.content.Context
import eu.kanade.tachiyomi.source.model.Filter
import tachiyomi.core.common.i18n.stringResource
import tachiyomi.i18n.MR

sealed class MangaOrderBy(context: Context, selection: Selection) : Filter.Sort(
    context.stringResource(MR.strings.local_filter_order_by),
    arrayOf(context.stringResource(MR.strings.title), context.stringResource(MR.strings.date)),
    selection,
) {
    class Popular(context: Context) : MangaOrderBy(context, Selection(0, true))
    class Latest(context: Context) : MangaOrderBy(context, Selection(1, false))
}
