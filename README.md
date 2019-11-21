


# Points
1. My base idea in this project is to use Androids latest Paging library with the supported impl for
handling the loading of items list data in the view, and the controller (Frag, Activity). It offers out of the box
support for paging, both locally and network paging when is available as an option on the endpoint. There is a BoundaryCallback
which handles the events such as zero items, reached top or bottom of the list when data is loaded from the Room db. Since its more
convenient and framework supported way, I decided it is the best way currently to be used for the list loading. Its a bit different
approach from legacy implementations of the paging, regarding the data loading, and the adapter, which has a bit straight forward way
of usage, with few things restricted (like immutable list of items, and other callbacks not available, in comparison with the
other adapter interfaces and implementations.
Also, I`m using navigation library, I`m using this lib since last year, since it was in alpha versions.
The idea is to show a list of products added in open source endpoint with exposed api. Its a list of food
products. Wehn an item is taped, Details view is opened, with a more detailed information about the product.
Tehres an option to search and sort the items.

# Features

1. Enabled TLSv1.2 support for connecting on older versions pre 20 Apis

2. Main view - loads the items from the https://datakick.org api, which endpoint supports paging. Paging is
handled with the next page taken from the response headers. When swiped to refresh, table is cleared, and
items are loaded from the endpoint beginning with the first page.
Theres an option to search for items by their name, and to sort them according the given two options.
Network errors are presented in the network state adapter item, better solution would be as
to be placed in the separate result error fragment, but just for the porpuse of the excercise are
left there.

3. Details view - Loads the data from the item received from the intent, as cached item,
and then loads the data from the network, to show the most relevant result. Test is written on the ViewModel,
but its very obscure, since theres not mutch logic to be tested, most of the logic is by mapping the
data betwen few LiveData sources. Also, most of the logic to be etsted, is moved inside the repository
implementation.
This view contains extended information about the product. I`m using COnstraint layout almost everywhere.

4. Repository and DB - Repo implementation contains and holds few live data objects, which are passed
to the receivers and observers further more. This is where is the logic for handling the local and remote data,
handling the list paging and other stuff.

5. Di and Dagger - is used where is necessary.


