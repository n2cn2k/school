# search.py
# ---------
# Licensing Information:  You are free to use or extend these projects for
# educational purposes provided that (1) you do not distribute or publish
# solutions, (2) you retain this notice, and (3) you provide clear
# attribution to UC Berkeley, including a link to http://ai.berkeley.edu.
# 
# Attribution Information: The Pacman AI projects were developed at UC Berkeley.
# The core projects and autograders were primarily created by John DeNero
# (denero@cs.berkeley.edu) and Dan Klein (klein@cs.berkeley.edu).
# Student side autograding was added by Brad Miller, Nick Hay, and
# Pieter Abbeel (pabbeel@cs.berkeley.edu).


"""
In search.py, you will implement generic search algorithms which are called by
Pacman agents (in searchAgents.py).
"""

import util

class SearchProblem:
    """
    This class outlines the structure of a search problem, but doesn't implement
    any of the methods (in object-oriented terminology: an abstract class).

    You do not need to change anything in this class, ever.
    """

    def getStartState(self):
        """
        Returns the start state for the search problem.
        """
        util.raiseNotDefined()

    def isGoalState(self, state):
        """
          state: Search state

        Returns True if and only if the state is a valid goal state.
        """
        util.raiseNotDefined()

    def getSuccessors(self, state):
        """
          state: Search state

        For a given state, this should return a list of triples, (successor,
        action, stepCost), where 'successor' is a successor to the current
        state, 'action' is the action required to get there, and 'stepCost' is
        the incremental cost of expanding to that successor.
        """
        util.raiseNotDefined()

    def getCostOfActions(self, actions):
        """
         actions: A list of actions to take

        This method returns the total cost of a particular sequence of actions.
        The sequence must be composed of legal moves.
        """
        util.raiseNotDefined()


def tinyMazeSearch(problem):
    """
    Returns a sequence of moves that solves tinyMaze.  For any other maze, the
    sequence of moves will be incorrect, so only use this for tinyMaze.
    """
    from game import Directions
    s = Directions.SOUTH
    w = Directions.WEST
    return  [s, s, w, s, w, w, s, w]
    

def depthFirstSearch(problem):
    """
    Search the deepest nodes in the search tree first.

    Your search algorithm needs to return a list of actions that reaches the
    goal. Make sure to implement a graph search algorithm.

    To get started, you might want to try some of these simple commands to
    understand the search problem that is being passed in:

    print "Start:", problem.getStartState()
    print "Is the start a goal?", problem.isGoalState(problem.getStartState())
    print "Start's successors:", problem.getSuccessors(problem.getStartState())
    """
    return solution_entry(DFS, problem)
    #util.raiseNotDefined()

def breadthFirstSearch(problem):
    """Search the shallowest nodes in the search tree first."""
    "*** YOUR CODE HERE ***"
    return solution_entry(BFS, problem)
    #util.raiseNotDefined()

def uniformCostSearch(problem):
    """Search the node of least total cost first."""
    "*** YOUR CODE HERE ***"
    return solution_entry(UCS, problem)
    #util.raiseNotDefined()

def nullHeuristic(state, problem=None):
    """
    A heuristic function estimates the cost from the current state to the nearest
    goal in the provided SearchProblem.  This heuristic is trivial.
    """
    return 0

def aStarSearch(problem, heuristic=nullHeuristic):
    """Search the node that has the lowest combined cost and heuristic first."""
    "*** YOUR CODE HERE ***"
    return solution_entry(ASTAR, problem, heuristic)
    #util.raiseNotDefined()


# Abbreviations
bfs = breadthFirstSearch
dfs = depthFirstSearch
astar = aStarSearch
ucs = uniformCostSearch




###MY CODE BEGINS HERE###
#########################

class Search(object):
    class Node:
        def __init__(self, state=None, parent=None, action=None, cost=None):
            self.state = state
            self.parent = parent
            self.action = action
            self.cost = cost
    
        
    def __init__(self, problem, frontier):
        self.problem = problem
        self.frontier = frontier
        self.target = None
        self.root = self.Node(self.problem.getStartState(), None, None, 0)
        self.known_states = {self.root.state: self.root}
        
    
    def framework(self):
        while not self.frontier.isEmpty():
            node = self.pick_next()
            if self.is_goal(node):
                #continue
                break
            self.merge(self.expand(node))
            
        if self.target is not None:    
            path = self.deduce(self.target)
            #self.output(path)
            return [step.action for step in path]
        return []
        
    
    def pick_next(self):
        return self.frontier.pop()

        
    def is_goal(self, node):
        if self.problem.isGoalState(node.state):
            #print('{0} vs {1}'.format(self.target.cost if self.target is not None else -1, node.cost))
            if self.target is None or self.target.cost > node.cost:
                self.target = node
                #print("{0}{1}".format(self.node2str(node), 'Success!'))
                return True
        return False

        
    def is_new(self, node):
        return node.state not in self.known_states.keys()

        
    def is_cheap(self, state, cost):
        old_cost = self.known_states[state].cost
        return old_cost is not None and old_cost > cost 
        #Here we don't care the hn(state). WHY? -- costs are SAME for "two" IDENTICAL states.

        
    def expand(self, node):
        expanded_list = []
        for [state, action, effort] in self.problem.getSuccessors(node.state):
            gn = self.eval_gn(node.cost, effort)
            child_node = self.Node(state, node, action, gn)
            if self.is_new(child_node) or self.is_cheap(state, gn):
                self.known_states[state] = child_node
                expanded_list.append(child_node)
        return expanded_list

        
    def eval_hn(self, child_state): 
        return 0                   
        #h(n) is default to 0                 
    
    
    def eval_gn(self, parent_cost, child_step_effort): 
        gn = parent_cost + child_step_effort
        return gn                                      
    
    
    def merge(self, nodes):
        for node in nodes:
            self.frontier.push(node)
        #print [self.node2str(node) for node in self.frontier.list_all()]
        pass
        
        
    def deduce(self, node):
        path = []
        while node.parent:
            path.append(node)
            node = node.parent
        path.reverse()
        return path

    
    def loop_detect(self, node):
        if not node:
            return True
        tmp = node
        while tmp.parent and tmp.parent.state != node.state:
            tmp = tmp.parent
        return not tmp.parent
    
    
    def output(self, solution):
        if solution:
            print len(solution), ' => '.join([self.node2str(step) for step in solution])

            
    def node2str(self, node):
        return '-'.join([str(node.state), str(node.action), str(node.cost)])
    
    
        
class DFS(Search):
    def __init__(self, problem):
        super(DFS, self).__init__(problem, util.Stack())
        self.frontier.push(self.root)
        self.position_search_type = type(problem.getStartState()) == type((1,2))
        
    
    def is_new(self, node):
        result = super(DFS, self).is_new(node)
        if self.position_search_type:
            return result
        if not result:
            result = self.loop_detect(node)
        return result

        

class BFS(Search):
    def __init__(self, problem):
        super(BFS, self).__init__(problem, util.Queue())
        self.frontier.push(self.root)

        
        
class UCS(Search):
    def __init__(self, problem):
        super(UCS, self).__init__(problem, util.PriorityQueue())
        self.frontier.push(self.root.state, self.root.cost)

        
    def pick_next(self):
        state = self.frontier.pop()
        return self.known_states[state]

        
    def merge(self, nodes):
        for node in nodes:
            self.frontier.update(node.state, node.cost + self.eval_hn(node.state))
        
        
   
class ASTAR(UCS):
    def __init__(self, problem, heuristic):
        super(ASTAR, self).__init__(problem)
        self.heuristic = heuristic
    
    
    def eval_hn(self, state):
        hn = self.heuristic(state, self.problem)
        return hn
        
        
        
def solution_entry(strategy, *parameters):
    search = strategy(*parameters)
    return search.framework()

#########################
###MY CODE ENDS HERE###

