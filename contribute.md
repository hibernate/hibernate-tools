<!--
  ~ Hibernate Tools, Tooling for your Hibernate Projects
  ~
  ~ Copyright 2020 Red Hat, Inc.
  ~
  ~ Licensed under the GNU Lesser General Public License (LGPL), 
  ~ version 2.1 or later (the "License").
  ~ You may not use this file except in compliance with the License.
  ~ You may read the licence in the 'lgpl.txt' file in the root folder of 
  ~ project or obtain a copy at
  ~
  ~     http://www.gnu.org/licenses/lgpl-2.1.html
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" basis,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
  -->

# Contribution guide

**Want to contribute? Great!** 
We try to make it easy, and all contributions, even the smaller ones, are more than welcome.
This includes bug reports, fixes, documentation, examples... 
But first, read this page (including the small print at the end).

## Legal

All original contributions to Hibernate Tools are licensed under the
[GNU Lesser General Public License (LGPL)](http://www.gnu.org/licenses/lgpl-2.1.html),
version 2.1 or later, or, if another license is specified as governing the file or directory being
modified, such other license.

All contributions are subject to the [Developer Certificate of Origin (DCO)](https://developercertificate.org/).
The DCO text is also included verbatim in the [dco.txt](./dco.txt) file in the root directory of the repository.

## Reporting an issue

This project uses the [Hibernate Tools JIRA](https://hibernate.atlassian.net/projects/HBX/summary) to manage bugs and feature requests. Open an issue directly in JIRA.

If you believe you found a bug, and it's likely possible, please indicate a way to reproduce it, what you are seeing and what you would expect to see.
Don't forget to indicate your Java and Hibernate versions. 

## Before you contribute

To contribute, use GitHub Pull Requests, from your **own** fork.

## Import the Projects

The project is developed under the form of a hierarchical multimodule Maven project. 
After cloning the git repository to your local machine you can import it as such into 
your favorite IDE.

## Work on a Topic Branch

When you want to contribute code or documentation you first need to create a topic branch. It is common practice to give your branch a name that refers to the issue you are solving. E.g. if you want to fix the JIRA issue HBX-2000, name your topic branch 'HBX-2000' (`git checkout -b HBX-2000`). 

When the work is done or the issue is (partly) fixed, squash the commits into one. It is good practice to refer to the solved issue in the commit message. E.g. `git commit -m "HBX-2000: Create README.md files for the parent and child modules of the project - Add a 'contribute.md' file"`. 

Rebase your topic branch against the current master branch if necessary (`git rebase master`), push the branch to your own fork on GitHub (`git push <your-fork> HBX-2000`) and open a pull request (`https://github.com/<your-fork>/hibernate-tools/pull/new/HBX-2000`).

## The small print

This project is an open source project, please act responsibly, be nice, polite and enjoy!
